package com.example.uniregnative.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uniregnative.data.Course

/**
 * "Course Details" interface — reached from the Profile screen's "View Details"
 * button. Shows the full record for one registered course (Read), with a
 * Delete action ("Remove from My Courses") so this screen also satisfies a
 * CRUD pair on its own.
 */
@Composable
fun CourseDetailScreen(
    course: Course,
    onRemoveCourse: (Course) -> Unit, // Delete
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Course Details", style = MaterialTheme.typography.headlineSmall)

        Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(course.id, style = MaterialTheme.typography.titleLarge)
                Text(course.title, style = MaterialTheme.typography.titleMedium)

                Text(
                    "Instructor: ${course.instructor}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    "Credits: ${course.credits}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "Capacity: ${course.enrolled}/${course.capacity}" +
                            if (course.isFull) " (FULL)" else "",
                    style = MaterialTheme.typography.bodyMedium,
                )

                Text(
                    "Class Times",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 12.dp),
                )
                course.slots.forEach { slot ->
                    Text(
                        "${slot.day} " +
                                "${slot.startMinutes / 60}:${(slot.startMinutes % 60).toString().padStart(2, '0')}-" +
                                "${slot.endMinutes / 60}:${(slot.endMinutes % 60).toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        OutlinedButton(
            onClick = { onRemoveCourse(course) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            Text("Remove from My Courses")
        }
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("Back")
        }
    }
}