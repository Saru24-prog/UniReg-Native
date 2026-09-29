package com.example.uniregnative.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    // ---- Catalog (all available courses) ----

    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    // ---- Student's selected/registered courses ----

    @Query("""
        SELECT courses.* FROM courses
        INNER JOIN registered_courses ON courses.id = registered_courses.courseId
    """)
    fun getRegisteredCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM registered_courses WHERE courseId = :courseId")
    suspend fun getRegistration(courseId: String): RegisteredCourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRegistration(registration: RegisteredCourseEntity)

    @Update
    suspend fun updateRegistration(registration: RegisteredCourseEntity)

    @Query("DELETE FROM registered_courses WHERE courseId = :courseId")
    suspend fun removeRegistration(courseId: String)
}