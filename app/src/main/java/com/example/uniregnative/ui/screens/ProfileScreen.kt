package com.example.uniregnative.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uniregnative.data.Account
import com.example.uniregnative.data.Course

/**
 * "Profile" / "Dashboard" interface — matches Milestone 02 screen 02_dashboard.
 *
 * Shows a greeting for the signed-in account and the list of courses the
 * student has actually confirmed registration for (Read). Each course can
 * be inspected (View Details) or removed (Delete), and the "Register"
 * quick action is the primary, highlighted entry point back into course
 * selection.
 */
@Composable
fun ProfileScreen(
    account: Account?,
    registeredCourses: List<Course>,
    onRegisterMore: () -> Unit,
    onViewTimetable: () -> Unit,
    onViewCourseDetails: (Course) -> Unit,
    onDeleteCourse: (Course) -> Unit, // Delete
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Hi, ${account?.fullName ?: "Student"}",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            "${account?.studentId ?: ""} · ${account?.faculty ?: ""}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
        )

        Text("QUICK ACTIONS", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // "Register" is the highlighted primary action (filled Button), matching
            // the wireframe's emphasis on getting the student back into registration.
            Button(
                onClick = onRegisterMore,
                modifier = Modifier.weight(1f),
            ) {
                Text("Register")
            }
            OutlinedButton(
                onClick = onViewTimetable,
                modifier = Modifier.weight(1f),
            ) {
                Text("My Timetable")
            }
        }

        Text("MY COURSES", style = MaterialTheme.typography.labelLarge)

        if (registeredCourses.isEmpty()) {
            Text(
                "You haven't registered for any courses yet. Tap Register to get started.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(460.dp).padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(registeredCourses) { course ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(course.id, style = MaterialTheme.typography.titleSmall)
                            Text(course.title, style = MaterialTheme.typography.bodyMedium)
                            course.slots.forEach { slot ->
                                Text(
                                    "${slot.day} " +
                                            "${slot.startMinutes / 60}:${(slot.startMinutes % 60).toString().padStart(2, '0')}-" +
                                            "${slot.endMinutes / 60}:${(slot.endMinutes % 60).toString().padStart(2, '0')}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                OutlinedButton(
                                    onClick = { onViewCourseDetails(course) },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("View Details")
                                }
                                OutlinedButton(
                                    onClick = { onDeleteCourse(course) },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}