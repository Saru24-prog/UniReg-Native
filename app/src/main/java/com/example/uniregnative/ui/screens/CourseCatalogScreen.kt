package com.example.uniregnative.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uniregnative.data.Course
import com.example.uniregnative.data.SampleData
import kotlinx.coroutines.launch

private val ACCENT = Color(0xFF6366F1)
private val TITLE_COLOR = Color(0xFF111827)

/**
 * "Select Courses" interface — matches Milestone 02 screen 03_course_selection.
 *
 * Students can freely tick/untick any course (Create/Delete CRUD on the selection).
 * Clash detection is deliberately NOT enforced here — per the Milestone 02 design,
 * clashes are only surfaced later, on the Timetable Preview screen, after the
 * student taps "Preview Timetable".
 *
 * The course list is backed by [SampleData.catalog], which can be refreshed
 * live from Supabase (the "Sync" button below) — demonstrating that course
 * data is driven by a remote source, not hardcoded, without rebuilding the app.
 *
 * The notification bell that used to live here was removed since it's already
 * in the shared top header with its badge count — but the "Hi, name" greeting
 * stays, now as plain styled text instead of a bright default-blue button.
 */
@Composable
fun CourseCatalogScreen(
    selectedCourses: SnapshotStateList<Course>,
    accountName: String = "",
    profilePhoto: ImageBitmap? = null,
    onOpenProfile: () -> Unit = {},
    notificationCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    onPreviewTimetable: () -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSyncing by remember { mutableStateOf(false) }
    var syncStatus by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    fun syncCourses() {
        isSyncing = true
        coroutineScope.launch {
            val success = SampleData.refreshFromSupabase()
            syncStatus = if (success) "✓ Synced from server" else "⚠ Offline — showing cached courses"
            isSyncing = false
        }
    }

    // Sync once automatically when this screen first appears.
    LaunchedEffect(Unit) { syncCourses() }

    val filteredCourses = SampleData.catalog.filter { course ->
        val query = searchQuery.trim()
        query.isEmpty() ||
                course.title.contains(query, ignoreCase = true) ||
                course.id.contains(query, ignoreCase = true) ||
                course.instructor.contains(query, ignoreCase = true)
    }

    fun toggleCourse(course: Course) {
        if (selectedCourses.any { it.id == course.id }) {
            selectedCourses.removeAll { it.id == course.id } // Delete
        } else {
            selectedCourses.add(course) // Create
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    "Select Courses",
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                    color = TITLE_COLOR,
                )
                Text(
                    "Choose your courses for this semester",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Text(
                "Hi, ${accountName.substringBefore(" ").ifBlank { "Student" }} 👋",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = ACCENT,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { onOpenProfile() },
            )
        }
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                when {
                    isSyncing -> "Syncing courses..."
                    syncStatus != null -> syncStatus!!
                    else -> ""
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = { syncCourses() }, enabled = !isSyncing) {
                Text(
                    if (isSyncing) "⟳ Syncing" else "⟳ Sync",
                    color = ACCENT,
                )
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search courses...") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ACCENT,
                focusedLabelColor = ACCENT,
                cursorColor = ACCENT,
            ),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        )

        if (filteredCourses.isEmpty()) {
            Text(
                "No courses match \"$searchQuery\".",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(380.dp).padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(filteredCourses) { course ->
                val isSelected = selectedCourses.any { it.id == course.id }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(course.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${course.id} • ${course.instructor} • ${course.credits} credits",
                                style = MaterialTheme.typography.bodySmall,
                            )
                            course.slots.forEach { slot ->
                                Text(
                                    "${slot.day}: " +
                                            "${slot.startMinutes / 60}:" +
                                            "${(slot.startMinutes % 60).toString().padStart(2, '0')} - " +
                                            "${slot.endMinutes / 60}:" +
                                            "${(slot.endMinutes % 60).toString().padStart(2, '0')}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            if (course.isFull && !isSelected) {
                                Text(
                                    "FULL",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { toggleCourse(course) },
                            enabled = isSelected || !course.isFull,
                            colors = CheckboxDefaults.colors(checkedColor = ACCENT),
                        )
                    }
                }
            }
        }

        Text(
            "${selectedCourses.size} of ${SampleData.catalog.size} courses selected",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )

        Button(
            onClick = onPreviewTimetable,
            enabled = selectedCourses.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("Preview Timetable")
        }
    }
}