@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.uniregnative
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import android.widget.Toast
import android.os.Bundle
import androidx.compose.foundation.layout.size
import com.example.uniregnative.data.SupabaseCourseRepository
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
import com.example.uniregnative.data.SupabaseAccountRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
                val registeredCourses = remember {
                    mutableStateListOf<Course>().apply {
                        currentAccount?.let { addAll(AccountStore.loadRegisteredCourses(context, it.email)) }
                    }
                }
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
                                        coroutineScope.launch {
                                            SupabaseAccountRepository.insertAccount(account.fullName, account.email)
                                        }
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
                                val loadedPhoto = AccountStore.loadPhoto(context, account.email)
                                profilePhoto = loadedPhoto
                                AccountStore.saveLoggedInEmail(context, account.email)
                                registeredCourses.clear()
                                registeredCourses.addAll(AccountStore.loadRegisteredCourses(context, account.email))
                                screen = if (loadedPhoto != null) Screen.SELECT_COURSES else Screen.PHOTO_UPLOAD
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

                        else -> {
                            val mainScreens = setOf(Screen.SELECT_COURSES, Screen.TIMETABLE_PREVIEW, Screen.PROFILE)

                            Scaffold(
                                topBar = {
                                    if (screen in mainScreens) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .shadow(elevation = 6.dp, shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                                                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                                                .background(Color(0xFF111827))
                                                .statusBarsPadding()
                                                .padding(horizontal = 18.dp, vertical = 16.dp),
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(34.dp)
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(Color(0xFF6366F1)),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Text("🎓", fontSize = 18.sp)
                                                    }
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text(
                                                        "UniReg",
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 19.sp,
                                                        letterSpacing = 0.5.sp,
                                                    )
                                                }
                                                BadgedBox(
                                                    badge = {
                                                        if (notifications.isNotEmpty()) {
                                                            Badge(containerColor = Color(0xFFEF4444)) {
                                                                Text("${notifications.size}")
                                                            }
                                                        }
                                                    },
                                                ) {
                                                    IconButton(onClick = {
                                                        previousScreen = screen
                                                        screen = Screen.NOTIFICATIONS
                                                    }) {
                                                        Text("🔔", fontSize = 20.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                bottomBar = {
                                    if (screen in mainScreens) {
                                        NavigationBar(
                                            modifier = Modifier
                                                .shadow(elevation = 8.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                                            containerColor = Color(0xFF111827),
                                            contentColor = Color.White,
                                        ) {
                                            NavigationBarItem(
                                                selected = screen == Screen.SELECT_COURSES,
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = Color.White,
                                                    selectedTextColor = Color.White,
                                                    indicatorColor = Color(0xFF6366F1),
                                                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                                    unselectedTextColor = Color.White.copy(alpha = 0.6f),
                                                ),
                                                onClick = { screen = Screen.SELECT_COURSES },
                                                icon = { Text("🏠", fontSize = 20.sp) },
                                                label = { Text("Courses") },
                                            )
                                            NavigationBarItem(
                                                selected = screen == Screen.TIMETABLE_PREVIEW,
                                                onClick = { screen = Screen.TIMETABLE_PREVIEW },
                                                icon = { Text("🗓", fontSize = 20.sp) },
                                                label = { Text("Timetable") },
                                            )
                                            NavigationBarItem(
                                                selected = screen == Screen.PROFILE,
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = Color.White,
                                                    selectedTextColor = Color.White,
                                                    indicatorColor = Color(0xFF6366F1),
                                                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                                    unselectedTextColor = Color.White.copy(alpha = 0.6f),
                                                ),
                                                onClick = { screen = Screen.PROFILE },
                                                icon = { Text("👤", fontSize = 20.sp) },
                                                label = { Text("Profile") },
                                            )
                                        }
                                    }
                                },
                            ) { innerPadding ->
                                Box(modifier = Modifier.padding(innerPadding)) {
                                    when {
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
                                                    currentAccount?.let { acc ->
                                                        AccountStore.saveRegisteredCourses(context, acc.email, registeredCourses)
                                                    }
                                                    selectedCourses.forEach { course ->
                                                        coroutineScope.launch {
                                                            SupabaseCourseRepository.updateEnrolled(course.id, course.enrolled + 1)
                                                        }
                                                    }
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
                                                currentAccount?.let { acc ->
                                                    AccountStore.saveRegisteredCourses(context, acc.email, registeredCourses)
                                                }
                                                coroutineScope.launch {
                                                    SupabaseCourseRepository.updateEnrolled(
                                                        course.id,
                                                        (course.enrolled - 1).coerceAtLeast(0),
                                                    )
                                                }
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
                                            },
                                            onUpdateProfile = { newName, newFaculty ->
                                                currentAccount?.let { acc ->
                                                    val updated = acc.copy(fullName = newName, faculty = newFaculty)
                                                    val index = accounts.indexOfFirst { it.email.equals(acc.email, ignoreCase = true) }
                                                    if (index >= 0) {
                                                        accounts[index] = updated
                                                        AccountStore.saveAccounts(context, accounts)
                                                    }
                                                    currentAccount = updated
                                                }
                                            },
                                        )


                                        screen == Screen.COURSE_DETAIL && viewingCourse != null -> CourseDetailScreen(
                                            course = viewingCourse!!,
                                            onRemoveCourse = { course ->
                                                registeredCourses.removeAll { it.id == course.id }
                                                currentAccount?.let { acc ->
                                                    AccountStore.saveRegisteredCourses(context, acc.email, registeredCourses)
                                                }
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
            }
        }
    }
}