package com.example.uniregnative.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uniregnative.data.Course
import com.example.uniregnative.data.SampleData
import com.example.uniregnative.logic.Clash
import com.example.uniregnative.logic.ClashDetector

/**
 * "Clash Details" interface — matches Milestone 02 screen 06_clash_details.
 *
 * Offers alternative sections for the clashing course. This is the CRUD
 * "Update" operation for the Select Courses interface: applying an
 * alternative swaps out the clashing course for a non-clashing one. A
 * "Remove Course" action is also offered as the CRUD "Delete" operation,
 * so this interface satisfies the assignment's 2-CRUD-operations minimum.
 */
@Composable
fun ClashDetailsScreen(
    blockedCourse: Course,
    clash: Clash,
    currentlySelected: List<Course>,
    onApplyAlternative: (old: Course, new: Course) -> Unit, // Update
    onRemoveCourse: (Course) -> Unit, // Delete
    onBack: () -> Unit,
) {
    var selectedAlternative by remember { mutableStateOf<Course?>(null) }

    // An "alternative" is any other section of the same course (same base id,
    // e.g. "CS3021" -> "CS3021-ALT") that does not clash with what's already
    // selected once the blocked course is set aside.
    val baseId = blockedCourse.id.substringBefore("-")
    val remainingSelection = currentlySelected.filter { it.id != blockedCourse.id }
    val alternatives = SampleData.catalog.filter { candidate ->
        candidate.id != blockedCourse.id &&
                candidate.id.substringBefore("-") == baseId &&
                !ClashDetector.wouldClash(remainingSelection, candidate).hasClash
    }

    // Find courseB's slot on the same day as the clash, for the overlap caption.
    val otherCourse = if (clash.courseA.id == blockedCourse.id) clash.courseB else clash.courseA
    val otherSlotOnSameDay = otherCourse.slots.first { it.day == clash.slotA.day }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Clash Details", style = MaterialTheme.typography.headlineSmall)

        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    "${blockedCourse.id} ${blockedCourse.title}",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    "${clash.slotA.day} " +
                            "${clash.slotA.startMinutes / 60}:${(clash.slotA.startMinutes % 60).toString().padStart(2, '0')}-" +
                            "${clash.slotA.endMinutes / 60}:${(clash.slotA.endMinutes % 60).toString().padStart(2, '0')} " +
                            "— overlaps ${otherCourse.id} " +
                            "(${otherSlotOnSameDay.startMinutes / 60}:${(otherSlotOnSameDay.startMinutes % 60).toString().padStart(2, '0')}-" +
                            "${otherSlotOnSameDay.endMinutes / 60}:${(otherSlotOnSameDay.endMinutes % 60).toString().padStart(2, '0')})",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Text(
            "ALTERNATIVE TIMES FOR ${blockedCourse.id}",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
        )

        if (alternatives.isEmpty()) {
            Text(
                "No non-clashing alternative sections were found for this course.",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(alternatives) { alt ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = selectedAlternative?.id == alt.id,
                                onClick = { selectedAlternative = alt },
                            )
                            Column(modifier = Modifier.padding(start = 4.dp)) {
                                Text(alt.id, style = MaterialTheme.typography.titleSmall)
                                alt.slots.forEach { slot ->
                                    Text(
                                        "${slot.day} " +
                                                "${slot.startMinutes / 60}:${(slot.startMinutes % 60).toString().padStart(2, '0')}-" +
                                                "${slot.endMinutes / 60}:${(slot.endMinutes % 60).toString().padStart(2, '0')}",
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                                Text("0 clashes", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                selectedAlternative?.let { onApplyAlternative(blockedCourse, it) }
            },
            enabled = selectedAlternative != null,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        ) {
            Text("Apply Selected Time")
        }
        OutlinedButton(
            onClick = { onRemoveCourse(blockedCourse) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("Remove Course")
        }
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("Back to Clash Warning")
        }
    }
}