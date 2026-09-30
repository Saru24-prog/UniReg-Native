package com.example.uniregnative.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uniregnative.data.Course

/**
 * "Confirmation" interface — matches Milestone 02 screen 08_confirmation.
 *
 * Shown when the student taps "Confirm Registration" on the Timetable
 * Preview screen and no clashes remain (the CRUD "Read" view of the
 * final registered set).
 */
@Composable
fun ConfirmationScreen(
    registeredCourses: List<Course>,
    confirmationCode: String,
    onViewTimetable: () -> Unit,
    onDone: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Confirmation", style = MaterialTheme.typography.headlineSmall)

        Text(
            "✓",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = 24.dp),
        )
        Text(
            "Registration Complete",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            "No clashes remain in your timetable",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
        )

        Text(
            "REGISTERED COURSES",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth(),
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(300.dp).padding(top = 8.dp),
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
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text(
                "Confirmation code: $confirmationCode",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Button(
            onClick = onViewTimetable,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            Text("View My Timetable")
        }
        OutlinedButton(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("Done")
        }
    }
}