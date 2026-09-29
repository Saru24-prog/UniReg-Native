

package com.example.uniregnative.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.uniregnative.data.Course
import com.example.uniregnative.data.DayOfWeek
import com.example.uniregnative.data.TimeSlot

/**
 * Room-persisted version of [Course]. Time slots can't be stored directly
 * in a SQLite column, so they're encoded to/from a compact string via
 * [Converters] (e.g. "MON:540:630|WED:540:630").
 */
@Entity(tableName = "courses")
@TypeConverters(Converters::class)
data class CourseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val instructor: String,
    val credits: Int,
    val slots: List<TimeSlot>,
    val capacity: Int,
    val enrolled: Int,
)

/** Maps a persisted [CourseEntity] back to the plain domain [Course]. */
fun CourseEntity.toDomain(): Course = Course(
    id = id,
    title = title,
    instructor = instructor,
    credits = credits,
    slots = slots,
    capacity = capacity,
    enrolled = enrolled,
)

/** Maps a domain [Course] to its persisted [CourseEntity] form. */
fun Course.toEntity(): CourseEntity = CourseEntity(
    id = id,
    title = title,
    instructor = instructor,
    credits = credits,
    slots = slots,
    capacity = capacity,
    enrolled = enrolled,
)

/**
 * Room type converters. Room can only store primitive/String columns, so
 * this teaches it how to turn a List<TimeSlot> into a single String column
 * and back.
 */
class Converters {

    @TypeConverter
    fun fromSlotList(slots: List<TimeSlot>): String =
        slots.joinToString("|") { "${it.day}:${it.startMinutes}:${it.endMinutes}" }

    @TypeConverter
    fun toSlotList(raw: String): List<TimeSlot> {
        if (raw.isBlank()) return emptyList()
        return raw.split("|").map { part ->
            val (day, start, end) = part.split(":")
            TimeSlot(DayOfWeek.valueOf(day), start.toInt(), end.toInt())
        }
    }
}