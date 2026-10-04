package com.example.uniregnative.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private fun decodeBitmapFromUri(context: Context, uri: Uri): ImageBitmap? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)?.asImageBitmap()
        }
    } catch (e: Exception) {
        null
    }
}

/**
 * Shown once right after a successful login: lets the student pick a
 * profile photo from their device gallery, or skip it. The chosen photo
 * (if any) is then shown next to "Hi, {name}" on Select Courses and in
 * the Profile header.
 */
@Composable
fun PhotoUploadScreen(
    accountName: String,
    onPhotoChosen: (ImageBitmap) -> Unit,
    onSkip: () -> Unit,
) {
    val context = LocalContext.current
    var previewPhoto by remember { mutableStateOf<ImageBitmap?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = decodeBitmapFromUri(context, it)
            if (bitmap != null) {
                previewPhoto = bitmap
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Add a profile photo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            "Help your classmates recognize you, $accountName",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp),
        )

        val currentPreview = previewPhoto
        if (currentPreview != null) {
            Image(
                bitmap = currentPreview,
                contentDescription = "Selected profile photo",
                modifier = Modifier.size(140.dp).clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        } else {
            val initial = accountName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "S"
            Column(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFFC857), Color(0xFFE85D75)),
                        ),
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(initial, color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
            }
        }

        Button(
            onClick = { pickImageLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
        ) {
            Text(if (currentPreview != null) "Choose a Different Photo" else "Upload Photo")
        }

        if (currentPreview != null) {
            Button(
                onClick = { onPhotoChosen(currentPreview) },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            ) {
                Text("Continue")
            }
        }

        TextButton(onClick = onSkip, modifier = Modifier.padding(top = 8.dp)) {
            Text("Skip for now")
        }
    }
}