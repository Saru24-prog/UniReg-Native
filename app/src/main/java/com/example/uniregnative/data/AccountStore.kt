package com.example.uniregnative.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Persists accounts, the current login session, each account's profile
 * photo, and each account's registered courses to local device storage
 * (SharedPreferences / internal files), so all of this survives logout,
 * app restart, or rebuild — only notifications and draft course selections
 * stay in-memory-only per session.
 */
object AccountStore {
    private const val PREFS_NAME = "unireg_prefs"
    private const val KEY_ACCOUNTS = "accounts"
    private const val KEY_LOGGED_IN_EMAIL = "logged_in_email"
    private const val KEY_REGISTERED_COURSES_PREFIX = "registered_courses_"

    fun loadAccounts(context: Context): List<Account> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_ACCOUNTS, null) ?: return emptyList()
        val array = JSONArray(json)
        val result = mutableListOf<Account>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            result.add(
                Account(
                    fullName = obj.getString("fullName"),
                    studentId = obj.getString("studentId"),
                    email = obj.getString("email"),
                    faculty = obj.getString("faculty"),
                    password = obj.getString("password"),
                ),
            )
        }
        return result
    }

    fun saveAccounts(context: Context, accounts: List<Account>) {
        val array = JSONArray()
        accounts.forEach { account ->
            val obj = JSONObject()
            obj.put("fullName", account.fullName)
            obj.put("studentId", account.studentId)
            obj.put("email", account.email)
            obj.put("faculty", account.faculty)
            obj.put("password", account.password)
            array.put(obj)
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ACCOUNTS, array.toString())
            .apply()
    }

    fun saveLoggedInEmail(context: Context, email: String?) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LOGGED_IN_EMAIL, email)
            .apply()
    }

    fun loadLoggedInEmail(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LOGGED_IN_EMAIL, null)
    }

    fun savePhoto(context: Context, email: String, bitmap: ImageBitmap) {
        try {
            val fileName = photoFileName(email)
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { out ->
                bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
        } catch (e: Exception) {
            // Photo just won't persist this time — not fatal.
        }
    }

    fun loadPhoto(context: Context, email: String): ImageBitmap? {
        return try {
            val fileName = photoFileName(email)
            if (!context.getFileStreamPath(fileName).exists()) return null
            context.openFileInput(fileName).use { input ->
                BitmapFactory.decodeStream(input)?.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun photoFileName(email: String): String =
        "photo_" + sanitize(email) + ".jpg"

    /**
     * Saves this account's registered courses so they survive logout/login
     * and app restarts, instead of only living in MainActivity's in-memory
     * list for the current session.
     */
    fun saveRegisteredCourses(context: Context, email: String, courses: List<Course>) {
        val array = JSONArray()
        courses.forEach { course ->
            val obj = JSONObject()
            obj.put("id", course.id)
            obj.put("title", course.title)
            obj.put("instructor", course.instructor)
            obj.put("credits", course.credits)
            obj.put("capacity", course.capacity)
            obj.put("enrolled", course.enrolled)
            val slotsArray = JSONArray()
            course.slots.forEach { slot ->
                val slotObj = JSONObject()
                slotObj.put("day", slot.day.name)
                slotObj.put("startMinutes", slot.startMinutes)
                slotObj.put("endMinutes", slot.endMinutes)
                slotsArray.put(slotObj)
            }
            obj.put("slots", slotsArray)
            array.put(obj)
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_REGISTERED_COURSES_PREFIX + sanitize(email), array.toString())
            .apply()
    }

    fun loadRegisteredCourses(context: Context, email: String): List<Course> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_REGISTERED_COURSES_PREFIX + sanitize(email), null)
            ?: return emptyList()
        val array = JSONArray(json)
        val result = mutableListOf<Course>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val slotsArray = obj.getJSONArray("slots")
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
            result.add(
                Course(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    instructor = obj.getString("instructor"),
                    credits = obj.getInt("credits"),
                    slots = slots,
                    capacity = obj.getInt("capacity"),
                    enrolled = obj.getInt("enrolled"),
                ),
            )
        }
        return result
    }

    private fun sanitize(email: String): String =
        email.lowercase().replace(Regex("[^a-z0-9]"), "_")
}