package com.example.uniregnative.ui.screens

import androidx.compose.foundation.Image

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uniregnative.data.Account
import com.example.uniregnative.data.Course


/**
 * "Profile" / "Dashboard" interface — matches Milestone 02 screen 02_dashboard,
 * restyled with a gradient header banner, an avatar photo (or initial), and
 * accented course cards for a more polished look.
 */
@Composable
fun ProfileScreen(
    account: Account?,
    registeredCourses: List<Course>,
    profilePhoto: ImageBitmap? = null,
    onRegisterMore: () -> Unit,
    onViewTimetable: () -> Unit,
    onViewCourseDetails: (Course) -> Unit,
    onDeleteCourse: (Course) -> Unit, // Delete
    onLogout: () -> Unit = {},
) {
    val initials = (account?.fullName?.trim()?.firstOrNull()?.uppercaseChar() ?: 'S').toString()

    Column(modifier = Modifier.fillMaxSize()) {
        // Gradient header banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1C2B63), Color(0xFF3A2E73), Color(0xFF6A4FB8)),
                    ),
                )
                .padding(horizontal = 20.dp, vertical = 28.dp),
        ) {
            Text(
                "Logout",
                color = Color .White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable {onLogout()}
                    .padding(6 .dp),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (profilePhoto != null) {
                    Image(
                        bitmap = profilePhoto,
                        contentDescription = "Profile photo",
                        modifier = Modifier.size(64.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFFFC857), Color(0xFFE85D75)),
                                ),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(initials, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "Hi, ${account?.fullName ?: "Student"}",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "${account?.studentId ?: ""} · ${account?.faculty ?: ""}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Text(
                "QUICK ACTIONS",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 20.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = onRegisterMore,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text("➕ Register")
                }
                OutlinedButton(
                    onClick = onViewTimetable,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text("🗓 Timetable")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("MY COURSES", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        "${registeredCourses.size}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            if (registeredCourses.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("🗓", fontSize = 40.sp)
                    Text(
                        "You haven't registered for any courses yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Text(
                        "Tap Register to get started.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().height(430.dp).padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(registeredCourses) { course ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .fillMaxHeight()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color(0xFF4FD1C5), Color(0xFF6A4FB8)),
                                            ),
                                        ),
                                )
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                    ) {
                                        Text(
                                            course.id,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        )
                                    }
                                    Text(
                                        course.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(top = 6.dp),
                                    )
                                    course.slots.forEach { slot ->
                                        Text(
                                            "${slot.day} " +
                                                    "${slot.startMinutes / 60}:${(slot.startMinutes % 60).toString().padStart(2, '0')}-" +
                                                    "${slot.endMinutes / 60}:${(slot.endMinutes % 60).toString().padStart(2, '0')}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 2.dp),
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        OutlinedButton(
                                            onClick = { onViewCourseDetails(course) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                        ) {
                                            Text("View Details", fontSize = 12.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { onDeleteCourse(course) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error,
                                            ),
                                        ) {
                                            Text("🗑 Delete", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}