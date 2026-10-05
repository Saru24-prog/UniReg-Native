package com.example.uniregnative
import android.widget.Toast
import android.os.Bundle
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.example.uniregnative.data.Account
import com.example.uniregnative.data.AccountStore
import com.example.uniregnative.data.Course
import com.example.uniregnative.logic.Clash
import com.example.uniregnative.ui.screens.ClashDetailsScreen
import com.example.uniregnative.ui.screens.ClashWarningScreen
import com.example.uniregnative.ui.screens.ConfirmationScreen
import com.example.uniregnative.ui.screens.CourseCatalogScreen
import com.example.uniregnative.ui.screens.CourseDetailScreen
import com.example.uniregnative.ui.screens.LoginScreen
import com.example.uniregnative.ui.screens.NotificationItem
import com.example.uniregnative.ui.screens.NotificationsScreen
import com.example.uniregnative.ui.screens.PhotoUploadScreen
import com.example.uniregnative.ui.screens.ProfileScreen
import com.example.uniregnative.ui.screens.SignupScreen
import com.example.uniregnative.ui.screens.SplashScreen
import com.example.uniregnative.ui.screens.TimetableScreen
import com.example.uniregnative.ui.theme.UniRegNativeTheme


private enum class Screen {
    LOGIN,
    PHOTO_UPLOAD,
    SELECT_COURSES,
    TIMETABLE_PREVIEW,
    CLASH_WARNING,
    CLASH_DETAILS,
    CONFIRMATION,
    NOTIFICATIONS,
    PROFILE,
    COURSE_DETAIL,
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniRegNativeTheme {
                var showSplash by remember { mutableStateOf(true) }
                val context = LocalContext.current
                val coroutineScope = rememberCoroutineScope()
                val accounts = remember { mutableStateListOf<Account>().apply { addAll(AccountStore.loadAccounts(context)) } }
                val savedLoginEmail = remember { AccountStore.loadLoggedInEmail(context) }
                var currentAccount by remember {
                    mutableStateOf(accounts.find { it.email.equals(savedLoginEmail, ignoreCase = true) })
                }
                var loggedIn by remember { mutableStateOf(currentAccount != null) }
                var showSignup by remember { mutableStateOf(false) }
                var screen by remember { mutableStateOf(Screen.SELECT_COURSES) }
                var previousScreen by remember { mutableStateOf(Screen.SELECT_COURSES) }
                val selectedCourses = remember { mutableStateListOf<Course>() }
                var activeClash by remember { mutableStateOf<Clash?>(null) }
                var confirmationCode by remember { mutableStateOf("") }
                val notifications = remember { mutableStateListOf<NotificationItem>() }
                val registeredCourses = remember { mutableStateListOf<Course>() }
                var viewingCourse by remember { mutableStateOf<Course?>(null) }
                var profileReturnScreen by remember { mutableStateOf(Screen.SELECT_COURSES) }
                var profilePhoto by remember {
                    mutableStateOf(currentAccount?.let { AccountStore.loadPhoto(context, it.email) })
                }

                fun addNotification(title: String, message: String) {
                    notifications.add(
                        0,
                        NotificationItem(
                            id = "n${System.currentTimeMillis()}",
                            title = title,
                            message = message,
                            timestamp = "Just now",
                        ),
                    )
                    Toast.makeText(context, "$title: $message", Toast.LENGTH_LONG).show()
                }

                if (showSplash) {
                    SplashScreen(onFinished = { showSplash = false })
                } else {
                    when {
                        !loggedIn && showSignup -> SignupScreen(
                            onSignUp = { account ->
                                when {
                                    accounts.any { it.studentId.equals(account.studentId, ignoreCase = true) } ->
                                        "Student ID already registered"
                                    accounts.any { it.email.equals(account.email, ignoreCase = true) } ->
                                        "Email already registered"
                                    else -> {
                                        accounts.add(account)
                                        AccountStore.saveAccounts(context, accounts)
                                        null
                                    }
                                }
                            },
                            onNavigateToLogin = { showSignup = false },
                        )

                        !loggedIn -> LoginScreen(
                            accounts = accounts,
                            onLoginSuccess = { account ->
                                currentAccount = account
                                loggedIn = true
                                profilePhoto = AccountStore.loadPhoto(context, account.email)
                                AccountStore.saveLoggedInEmail(context, account.email)
                                screen = Screen.PHOTO_UPLOAD
                            },
                            onNavigateToSignup = { showSignup = true },
                        )

                        screen == Screen.PHOTO_UPLOAD -> PhotoUploadScreen(
                            accountName = currentAccount?.fullName ?: "Student",
                            onPhotoChosen = { bitmap ->
                                profilePhoto = bitmap
                                currentAccount?.let { AccountStore.savePhoto(context, it.email, bitmap) }
                                screen = Screen.SELECT_COURSES
                            },
                            onSkip = { screen = Screen.SELECT_COURSES },
                        )

                        screen == Screen.SELECT_COURSES -> CourseCatalogScreen(
                            selectedCourses = selectedCourses,
                            accountName = currentAccount?.fullName ?: "",
                            profilePhoto = profilePhoto,
                            onOpenProfile = {
                                profileReturnScreen = Screen.SELECT_COURSES
                                screen = Screen.PROFILE
                            },
                            notificationCount = notifications.size,
                            onOpenNotifications = {
                                previousScreen = Screen.SELECT_COURSES
                                screen = Screen.NOTIFICATIONS
                            },
                            onPreviewTimetable = { screen = Screen.TIMETABLE_PREVIEW },
                        )

                        screen == Screen.TIMETABLE_PREVIEW -> TimetableScreen(
                            selectedCourses = selectedCourses,
                            onConfirmRegistration = { result ->
                                if (result.hasClash) {
                                    val clash = result.clashes.first()
                                    activeClash = clash
                                    addNotification(
                                        "Timetable clash detected",
                                        "${clash.courseA.id} overlaps ${clash.courseB.id} on ${clash.slotA.day}",
                                    )
                                    screen = Screen.CLASH_WARNING
                                } else {
                                    confirmationCode = "REG-2026-${(10000..99999).random()}"
                                    registeredCourses.clear()
                                    registeredCourses.addAll(selectedCourses)
                                    addNotification(
                                        "Registration confirmed",
                                        "${selectedCourses.size} courses registered successfully",
                                    )
                                    screen = Screen.CONFIRMATION
                                }
                            },
                            onBack = { screen = Screen.SELECT_COURSES },
                        )

                        screen == Screen.CLASH_WARNING && activeClash != null -> ClashWarningScreen(
                            clash = activeClash!!,
                            onViewDetails = { screen = Screen.CLASH_DETAILS },
                            onCancel = { screen = Screen.TIMETABLE_PREVIEW },
                        )

                        screen == Screen.CLASH_DETAILS && activeClash != null -> ClashDetailsScreen(
                            blockedCourse = activeClash!!.courseB,
                            clash = activeClash!!,
                            currentlySelected = selectedCourses,
                            onApplyAlternative = { old, new ->
                                selectedCourses.removeAll { it.id == old.id }
                                selectedCourses.add(new)
                                activeClash = null
                                screen = Screen.TIMETABLE_PREVIEW
                            },
                            onRemoveCourse = { course ->
                                selectedCourses.removeAll { it.id == course.id }
                                activeClash = null
                                screen = Screen.TIMETABLE_PREVIEW
                            },
                            onBack = { screen = Screen.CLASH_WARNING },
                        )

                        screen == Screen.CONFIRMATION -> ConfirmationScreen(
                            registeredCourses = selectedCourses,
                            confirmationCode = confirmationCode,
                            onViewTimetable = { screen = Screen.TIMETABLE_PREVIEW },
                            onDone = { screen = Screen.SELECT_COURSES },
                        )

                        screen == Screen.NOTIFICATIONS -> NotificationsScreen(
                            notifications = notifications,
                            onDismiss = { item -> notifications.removeAll { it.id == item.id } },
                            onClearAll = { notifications.clear() },
                            onBack = { screen = previousScreen },
                        )

                        screen == Screen.PROFILE -> ProfileScreen(
                            account = currentAccount,
                            registeredCourses = registeredCourses,
                            profilePhoto = profilePhoto,
                            onRegisterMore = { screen = Screen.SELECT_COURSES },
                            onViewTimetable = {
                                selectedCourses.clear()
                                selectedCourses.addAll(registeredCourses)
                                screen = Screen.TIMETABLE_PREVIEW
                            },
                            onViewCourseDetails = { course ->
                                viewingCourse = course
                                screen = Screen.COURSE_DETAIL
                            },
                            onDeleteCourse = { course ->
                                registeredCourses.removeAll { it.id == course.id }
                            },
                            onLogout = {
                                AccountStore.saveLoggedInEmail(context, null)
                                currentAccount = null
                                loggedIn = false
                                showSignup = false
                                profilePhoto = null
                                selectedCourses.clear()
                                registeredCourses.clear()
                                screen = Screen.LOGIN
                            }

                        )

                        screen == Screen.COURSE_DETAIL && viewingCourse != null -> CourseDetailScreen(
                            course = viewingCourse!!,
                            onRemoveCourse = { course ->
                                registeredCourses.removeAll { it.id == course.id }
                                viewingCourse = null
                                screen = Screen.PROFILE
                            },
                            onBack = { screen = Screen.PROFILE },
                        )

                        else -> CourseCatalogScreen(
                            selectedCourses = selectedCourses,
                            accountName = currentAccount?.fullName ?: "",
                            profilePhoto = profilePhoto,
                            onOpenProfile = {
                                profileReturnScreen = Screen.SELECT_COURSES
                                screen = Screen.PROFILE
                            },
                            notificationCount = notifications.size,
                            onOpenNotifications = {
                                previousScreen = Screen.SELECT_COURSES
                                screen = Screen.NOTIFICATIONS
                            },
                            onPreviewTimetable = { screen = Screen.TIMETABLE_PREVIEW },
                        )
                    }
                }
            }
        }
    }
}