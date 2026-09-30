package com.example.uniregnative

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.uniregnative.ui.screens.LoginScreen
import com.example.uniregnative.ui.theme.UniRegNativeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniRegNativeTheme {
                LoginScreen()
            }
        }
    }
}