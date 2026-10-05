package com.example.uniregnative.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject


/**
 * Minimal Supabase REST client for read-only course catalog syncing.
 * Uses plain java.net HTTP + org.json (no new Gradle dependency) to GET
 * the `courses` table via Supabase's auto-generated REST API, and maps
 * each row into this app's Course/TimeSlot model.
 */
object SupabaseCourseRepository {
    private const val SUPABASE_URL = "https://rynkyfpoieekgtmmlvbg.supabase.co"
    private const val SUPABASE_ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJ5bmt5ZnBvaWVla2d0bW1sdmJnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTEwNDU5MjcsImV4cCI6MjEwNjYyMTkyN30.nyaodoKAiufG8cMfbAUCfnW6IATOkSwwv-E6i_BctWc"

    /**
     * Fetches all rows from the `courses` table. Returns null on any
     * failure (no internet, server error, bad response) so callers can
     * fall back to the built-in sample data instead of crashing.
     */

    suspend fun fetchCourses(): List<Course>? = withContext(Dispatchers.IO)
    {
        try {
            val url = URL("$SUPABASE_URL/rest/v1/courses?select=*")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY)
            connection.setRequestProperty("Authorization", "Bearer $SUPABASE_ANON_KEY")
            connection.connectTimeout = 8000
            connection.readTimeout = 8000

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                connection.disconnect()
                return@withContext null
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val rows = JSONArray(response)
            val courses = mutableListOf<Course>()
            for (i in 0 until rows.length()) {
                val row = rows.getJSONObject(i)
                val slotsArray = row.getJSONArray("slots")
                val slots = mutableListOf<TimeSlot>()
                for (j in 0 until slotsArray.length()) {
                    val slotObj = slotsArray.getJSONObject(j)
                    slots.add(
                        TimeSlot(
                            day = DayOfWeek.valueOf(slotObj.getString("day")),
                            startMinutes = slotObj.getInt("startMinutes"),
                            endMinutes = slotObj.getInt("endMinutes"),
                        ),
                    )
                }
                courses.add(
                    Course(
                        id = row.getString("id"),
                        title = row.getString("title"),
                        instructor = row.getString("instructor"),
                        credits = row.getInt("credits"),
                        slots = slots,
                        capacity = row.getInt("capacity"),
                        enrolled = row.getInt("enrolled"),
                    ),
                )
            }
            courses
        } catch (e: Exception) {
            null
        }
    }
    suspend fun updateEnrolled(courseId: String, newEnrolled: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$SUPABASE_URL/rest/v1/courses?id=eq.$courseId")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "PATCH"
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY)
            connection.setRequestProperty("Authorization", "Bearer $SUPABASE_ANON_KEY")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Prefer", "return=minimal")
            connection.doOutput = true
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val body = JSONObject().apply { put("enrolled", newEnrolled) }
            connection.outputStream.use { it.write(body.toString().toByteArray()) }
            val code = connection.responseCode
            connection.disconnect()
            code in 200..299
        } catch (e: Exception) {
            false
        }
    }
}