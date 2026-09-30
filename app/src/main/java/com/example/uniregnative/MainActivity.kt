package com.example.uniregnative

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.uniregnative.ui.screens.CourseCatalogScreen
import com.example.uniregnative.ui.screens.LoginScreen
import com.example.uniregnative.ui.theme.UniRegNativeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniRegNativeTheme {
                var loggedIn by remember { mutableStateOf(false) }
                if (loggedIn) {
                    CourseCatalogScreen()
                } else {
                    LoginScreen(onLoginSuccess = { loggedIn = true })
                }
            }
        }
    }
}