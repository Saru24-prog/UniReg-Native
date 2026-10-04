package com.example.uniregnative.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persists accounts and the current login session to local device storage
 * (SharedPreferences), so signup/login survive an app restart or rebuild —
 * unlike the rest of the app's in-memory state (courses, notifications,
 * profile photo), which is intentionally reset each session.
 */
object AccountStore {
    private const val PREFS_NAME = "unireg_prefs"
    private const val KEY_ACCOUNTS = "accounts"
    private const val KEY_LOGGED_IN_EMAIL = "logged_in_email"

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
}