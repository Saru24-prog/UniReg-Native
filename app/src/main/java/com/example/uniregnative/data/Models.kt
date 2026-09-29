package com.example.uniregnative.data

/**
 * Core data models for UniReg. Plain Kotlin data classes for the domain
 * logic layer; Room-annotated persistence entities (added next) map to
 * and from these.
 */

enum class DayOfWeek { MON, TUE, WED, THU, FRI, SAT, SUN }

/**
 * A single weekly time block, e.g. Mon 09:00-10:30.
 * Times are stored as minutes-from-midnight (0-1439) so overlap comparisons
 * are cheap integer comparisons instead of string/Date parsing.
 */
data class TimeSlot(
    val day: DayOfWeek,
    val startMinutes: Int,   // e.g. 9:00 AM -> 540
    val endMinutes: Int,     // e.g. 10:30 AM -> 630
) {
    init {
        require(startMinutes in 0..1439) { "startMinutes out of range" }
        require(endMinutes in 0..1439) { "endMinutes out of range" }
        require(startMinutes < endMinutes) { "startMinutes must be before endMinutes" }
    }

    /** True if this slot overlaps [other] on the same day. */
    fun overlaps(other: TimeSlot): Boolean {
        if (day != other.day) return false
        return startMinutes < other.endMinutes && other.startMinutes < endMinutes
    }

    companion object {
        /** Convenience constructor from "HH:mm" 24-hour strings, e.g. "09:00". */
        fun of(day: DayOfWeek, start: String, end: String): TimeSlot {
            fun parse(t: String): Int {
                val (h, m) = t.split(":").map { it.toInt() }
                return h * 60 + m
            }
            return TimeSlot(day, parse(start), parse(end))
        }
    }
}

data class Course(
    val id: String,          // e.g. "CS3060"
    val title: String,       // e.g. "Human-Computer Interaction"
    val instructor: String,
    val credits: Int,
    val slots: List<TimeSlot>,   // a course can meet more than once a week
    val capacity: Int = 0,
    val enrolled: Int = 0,
) {
    val isFull: Boolean get() = capacity > 0 && enrolled >= capacity
}

/** A course the student has added to their draft/registered timetable. */
data class RegisteredCourse(
    val course: Course,
    val status: RegistrationStatus = RegistrationStatus.DRAFT,
)

enum class RegistrationStatus { DRAFT, REGISTERED }