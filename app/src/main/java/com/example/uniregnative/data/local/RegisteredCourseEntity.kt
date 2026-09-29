package com.example.uniregnative.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.uniregnative.data.RegistrationStatus

/**
 * A course the student has added to their draft/registered timetable.
 * References a [CourseEntity] by id rather than embedding it, so the
 * catalog and the student's selections stay in separate tables.
 */
@Entity(tableName = "registered_courses")
data class RegisteredCourseEntity(
    @PrimaryKey val courseId: String,
    val status: RegistrationStatus,
)