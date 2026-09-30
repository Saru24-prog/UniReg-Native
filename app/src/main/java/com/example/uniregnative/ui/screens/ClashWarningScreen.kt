package com.example.uniregnative.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uniregnative.logic.Clash

/**
 * "Clash Warning" interface — matches Milestone 02 screen 05_clash_warning.
 *
 * Shown when the student taps "Confirm Registration" on the Timetable Preview
 * screen and at least one clash is present. Read-only summary of the clash;
 * the student either backs out (Cancel) or moves on to fix it
 * (View Details & Alternatives).
 */
@Composable
fun ClashWarningScreen(
    clash: Clash,
    onViewDetails: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Clash Warning", style = MaterialTheme.typography.headlineSmall)

        Text(
            "⚠",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = 24.dp),
        )
        Text(
            "Timetable Clash Detected",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            "The following classes overlap",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
        )

        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(clash.courseA.id, style = MaterialTheme.typography.titleMedium)
                Text(clash.courseA.title, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${clash.slotA.day} " +
                            "${clash.slotA.startMinutes / 60}:${(clash.slotA.startMinutes % 60).toString().padStart(2, '0')}-" +
                            "${clash.slotA.endMinutes / 60}:${(clash.slotA.endMinutes % 60).toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Text(
            "clashes with",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp),
        )

        // Find courseB's slot that falls on the same day as the clashing slotA,
        // so we can show its time without assuming a "slotB" field exists on Clash.
        val slotBOnSameDay = clash.courseB.slots.first { it.day == clash.slotA.day }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(clash.courseB.id, style = MaterialTheme.typography.titleMedium)
                Text(clash.courseB.title, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${slotBOnSameDay.day} " +
                            "${slotBOnSameDay.startMinutes / 60}:${(slotBOnSameDay.startMinutes % 60).toString().padStart(2, '0')}-" +
                            "${slotBOnSameDay.endMinutes / 60}:${(slotBOnSameDay.endMinutes % 60).toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text(
                "Overlap: ${clash.slotA.day}, " +
                        "${maxOf(clash.slotA.startMinutes, slotBOnSameDay.startMinutes) / 60}:" +
                        "${(maxOf(clash.slotA.startMinutes, slotBOnSameDay.startMinutes) % 60).toString().padStart(2, '0')}-" +
                        "${minOf(clash.slotA.endMinutes, slotBOnSameDay.endMinutes) / 60}:" +
                        "${(minOf(clash.slotA.endMinutes, slotBOnSameDay.endMinutes) % 60).toString().padStart(2, '0')}",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Button(
            onClick = onViewDetails,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        ) {
            Text("View Details & Alternatives")
        }
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("Cancel")
        }
    }
}