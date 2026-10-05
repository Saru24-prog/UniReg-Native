package com.example.uniregnative.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object SupabaseAccountRepository {
    private const val TAG = "UniRegSupabase"
    private const val SUPABASE_URL = "https://rynkyfpoieekgtmmlvbg.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJ5bmt5ZnBvaWVla2d0bW1sdmJnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTEwNDU5MjcsImV4cCI6MjEwNjYyMTkyN30.nyaodoKAiufG8cMfbAUCfnW6IATOkSwwv-E6i_BctWc"

    suspend fun insertAccount(name: String, email: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.e(TAG, "insertAccount() called with name=$name email=$email")
            val url = URL("$SUPABASE_URL/rest/v1/accounts")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY)
            connection.setRequestProperty("Authorization", "Bearer $SUPABASE_ANON_KEY")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Prefer", "return=minimal")
            connection.doOutput = true
            connection.connectTimeout = 8000
            connection.readTimeout = 8000

            val body = JSONObject().apply {
                put("name", name)
                put("email", email)
            }
            connection.outputStream.use { it.write(body.toString().toByteArray()) }

            val code = connection.responseCode
            Log.e(TAG, "insertAccount() response code = $code")

            if (code !in 200..299) {
                val errorText = connection.errorStream?.bufferedReader()?.readText()
                Log.e(TAG, "insertAccount() error body = $errorText")
            }

            connection.disconnect()
            code in 200..299
        } catch (e: Exception) {
            Log.e(TAG, "insertAccount() threw exception", e)
            false
        }
    }
}