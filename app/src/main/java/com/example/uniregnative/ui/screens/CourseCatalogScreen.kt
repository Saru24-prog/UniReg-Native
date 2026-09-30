package com.example.uniregnative.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uniregnative.data.Course
import com.example.uniregnative.data.SampleData
import com.example.uniregnative.logic.ClashDetector

/**
 * Course catalog: browse all offered courses and add them to your
 * selection. Warns immediately (no dialog needed) if adding a course
 * would clash with something already selected, matching the
 * "Clash Detection" flow from the Figma spec.
 */
@Composable
fun CourseCatalogScreen(
    onProceedToTimetable: (List<Course>) -> Unit = {},
) {
    val selected = remember { mutableStateListOf<Course>() }
    var clashWarning by remember { mutableStateOf<String?>(null) }

    fun toggleCourse(course: Course) {
        if (selected.any { it.id == course.id }) {
            selected.removeAll { it.id == course.id }
            clashWarning = null
            return
        }

        val result = ClashDetector.wouldClash(selected, course)
        if (result.hasClash) {
            val clash = result.clashes.first()
            val other = if (clash.courseA.id == course.id) clash.courseB else clash.courseA
            clashWarning = "${course.id} clashes with ${other.id} (${clash.slotA.day})"
        } else {
            selected.add(course)
            clashWarning = null
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Course Catalog",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "${selected.size} course(s) selected",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )

        clashWarning?.let {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ),
            ) {
                Text(
                    text = "⚠ Clash: $it",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(SampleData.catalog) { course ->
                val isSelected = selected.any { it.id == course.id }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(course.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${course.id} • ${course.instructor} • ${course.credits} credits",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                course.slots.joinToString(", ") { slot ->
                                    "${slot.day} ${slot.startMinutes / 60}:${(slot.startMinutes % 60).toString().padStart(2, '0')}"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            if (course.isFull) {
                                Text(
                                    "FULL",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                        Button(
                            onClick = { toggleCourse(course) },
                            enabled = isSelected || !course.isFull,
                        ) {
                            Text(if (isSelected) "Remove" else "Add")
                        }
                    }
                }
            }
        }

        Button(
            onClick = { onProceedToTimetable(selected.toList()) },
            enabled = selected.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        ) {
            Text("View Timetable (${selected.size})")
        }
    }
}