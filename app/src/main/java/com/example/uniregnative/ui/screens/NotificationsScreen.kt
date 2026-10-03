package com.example.uniregnative.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** A single notification event, created automatically when something happens in the app. */
data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
)

/**
 * "Notifications" interface — matches Milestone 02 screen 12_notifications.
 *
 * Unlike the mockup's static example feed, this list is driven by real app
 * events (a clash actually detected, a registration actually confirmed), so
 * it can be demonstrated live rather than just shown as a picture.
 *
 * CRUD: Create happens elsewhere (MainActivity adds an item when an event
 * fires); Delete happens here via "Dismiss" (one item) and "Clear All".
 */
@Composable
fun NotificationsScreen(
    notifications: List<NotificationItem>,
    onDismiss: (NotificationItem) -> Unit, // Delete
    onClearAll: () -> Unit, // Delete (bulk)
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Notifications", style = MaterialTheme.typography.headlineSmall)

        if (notifications.isEmpty()) {
            Text(
                "No notifications yet. You'll see updates here when a clash is detected " +
                        "or a registration is confirmed.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(500.dp).padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(notifications) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(item.title, style = MaterialTheme.typography.titleSmall)
                            Text(
                                "${item.message} · ${item.timestamp}",
                                style = MaterialTheme.typography.bodySmall,
                            )
                            OutlinedButton(
                                onClick = { onDismiss(item) },
                                modifier = Modifier.padding(top = 8.dp),
                            ) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = onClearAll,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Text("Clear All")
            }
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            Text("Back")
        }
    }
}