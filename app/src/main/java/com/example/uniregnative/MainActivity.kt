package com.example.uniregnative

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.uniregnative.data.Course
import com.example.uniregnative.logic.Clash
import com.example.uniregnative.ui.screens.ClashDetailsScreen
import com.example.uniregnative.ui.screens.ClashWarningScreen
import com.example.uniregnative.ui.screens.ConfirmationScreen
import com.example.uniregnative.ui.screens.CourseCatalogScreen
import com.example.uniregnative.ui.screens.LoginScreen
import com.example.uniregnative.ui.screens.TimetableScreen
import com.example.uniregnative.ui.theme.UniRegNativeTheme

private enum class Screen {
    LOGIN,
    SELECT_COURSES,
    TIMETABLE_PREVIEW,
    CLASH_WARNING,
    CLASH_DETAILS,
    CONFIRMATION,
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniRegNativeTheme {
                var loggedIn by remember { mutableStateOf(false) }
                var screen by remember { mutableStateOf(Screen.SELECT_COURSES) }
                val selectedCourses = remember { mutableStateListOf<Course>() }
                var activeClash by remember { mutableStateOf<Clash?>(null) }
                var confirmationCode by remember { mutableStateOf("") }

                when {
                    !loggedIn -> LoginScreen(
                        onLoginSuccess = {
                            loggedIn = true
                            screen = Screen.SELECT_COURSES
                        },
                    )

                    screen == Screen.SELECT_COURSES -> CourseCatalogScreen(
                        selectedCourses = selectedCourses,
                        onPreviewTimetable = { screen = Screen.TIMETABLE_PREVIEW },
                    )

                    screen == Screen.TIMETABLE_PREVIEW -> TimetableScreen(
                        selectedCourses = selectedCourses,
                        onConfirmRegistration = { result ->
                            if (result.hasClash) {
                                activeClash = result.clashes.first()
                                screen = Screen.CLASH_WARNING
                            } else {
                                confirmationCode = "REG-2026-${(10000..99999).random()}"
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

                    else -> CourseCatalogScreen(
                        selectedCourses = selectedCourses,
                        onPreviewTimetable = { screen = Screen.TIMETABLE_PREVIEW },
                    )
                }
            }
        }
    }
}