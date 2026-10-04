package com.example.uniregnative.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.uniregnative.data.Course
import com.example.uniregnative.data.DayOfWeek
import com.example.uniregnative.data.TimeSlot
import com.example.uniregnative.logic.ClashDetector
import com.example.uniregnative.logic.ClashResult

// One minute of class time = 0.6dp tall, and the grid starts showing from 8:00 AM.
private val MINUTE_HEIGHT: Dp = 0.6.dp
private const val GRID_START_MINUTES = 8 * 60
private const val GRID_END_MINUTES = 18 * 60
private val GRID_HEIGHT: Dp = MINUTE_HEIGHT * (GRID_END_MINUTES - GRID_START_MINUTES)
private val DAY_COLUMN_WIDTH: Dp = 100.dp
private val WEEK_DAYS = listOf(
    DayOfWeek.MON,
    DayOfWeek.TUE,
    DayOfWeek.WED,
    DayOfWeek.THU,
    DayOfWeek.FRI,
)

private fun timesOverlap(a: TimeSlot, b: TimeSlot): Boolean =
    a.startMinutes < b.endMinutes && b.startMinutes < a.endMinutes

/**
 * "Timetable Preview" interface — matches Milestone 02 screens
 * 04_timetable_preview (clash) / 21_timetable_preview_no_clash.
 *
 * Shows a weekly grid (Mon-Fri) with each selected course positioned by its
 * time slot. Courses that overlap in time on the same day are placed
 * side-by-side (instead of stacking on top of each other) with a "⚠" badge
 * and a red outline, so an overlap is always visible. A summary card at the
 * bottom reports clash count or confirms the timetable is clean.
 */
@Composable
fun TimetableScreen(
    selectedCourses: List<Course>,
    onConfirmRegistration: (ClashResult) -> Unit,
    onBack: () -> Unit,
) {
    val clashResult = ClashDetector.detect(selectedCourses)
    val clashedCourseIds = clashResult.affectedCourses.map { it.id }.toSet()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Timetable Preview", style = MaterialTheme.typography.headlineSmall)

        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            WEEK_DAYS.forEach { day ->
                Text(
                    day.name,
                    modifier = Modifier.width(DAY_COLUMN_WIDTH),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(GRID_HEIGHT)
                .padding(top = 4.dp)
                .horizontalScroll(rememberScrollState()),
        ) {
            WEEK_DAYS.forEach { day ->
                Box(
                    modifier = Modifier
                        .width(DAY_COLUMN_WIDTH)
                        .height(GRID_HEIGHT)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    // All (course, slot) pairs that land on this day, earliest first.
                    val daySlots = selectedCourses.flatMap { course ->
                        course.slots.filter { it.day == day }.map { slot -> course to slot }
                    }.sortedBy { it.second.startMinutes }

                    daySlots.forEach { (course, slot) ->
                        val isClashing = course.id in clashedCourseIds
                        // Every slot on this day that overlaps this one in time
                        // (including itself) — used to split overlapping courses into
                        // side-by-side columns instead of stacking on top of each other.
                        val overlapGroup = daySlots.filter { timesOverlap(it.second, slot) }
                        val columnCount = overlapGroup.size
                        val columnIndex = overlapGroup.indexOfFirst { (c, s) ->
                            c.id == course.id && s.startMinutes == slot.startMinutes && s.endMinutes == slot.endMinutes
                        }.coerceAtLeast(0)

                        val topOffset = MINUTE_HEIGHT * (slot.startMinutes - GRID_START_MINUTES)
                        val blockHeight = MINUTE_HEIGHT * (slot.endMinutes - slot.startMinutes)
                        val blockWidth = DAY_COLUMN_WIDTH / columnCount
                        val leftOffset = blockWidth * columnIndex

                        Box(
                            modifier = Modifier
                                .offset(x = leftOffset, y = topOffset)
                                .height(blockHeight)
                                .width(blockWidth)
                                .padding(1.dp)
                                .background(
                                    color = if (isClashing) {
                                        MaterialTheme.colorScheme.errorContainer
                                    } else {
                                        MaterialTheme.colorScheme.primaryContainer
                                    },
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .then(
                                    if (isClashing) {
                                        Modifier.border(
                                            width = 1.5.dp,
                                            color = MaterialTheme.colorScheme.error,
                                            shape = RoundedCornerShape(4.dp),
                                        )
                                    } else {
                                        Modifier
                                    },
                                )
                                .padding(3.dp),
                        ) {
                            Column {
                                Text(
                                    (if (isClashing) "⚠ " else "") + course.id,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                                Text(
                                    "${slot.startMinutes / 60}:" +
                                            "${(slot.startMinutes % 60).toString().padStart(2, '0')}",
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (clashResult.hasClash) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                },
            ),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (clashResult.hasClash) {
                    val firstClash = clashResult.clashes.first()
                    Text(
                        "⚠ ${clashResult.clashes.size} clash(es) detected",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        "${firstClash.courseA.id} overlaps ${firstClash.courseB.id} on ${firstClash.slotA.day}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                } else {
                    Text("✓ No clashes detected", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Timetable is ready to confirm",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        Button(
            onClick = { onConfirmRegistration(clashResult) },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        ) {
            Text("Confirm Registration")
        }
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("Back to Courses")
        }
    }
}