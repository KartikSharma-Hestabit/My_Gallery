package com.example.mygallery.screens

import android.graphics.drawable.Icon
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.Photo
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mygallery.R

sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Gallery : Screen("Gallery", R.string.gallery, Icons.Default.Photo)
    object MediaScreen : Screen("Media/{id}", R.string.media_screen, Icons.Default.PermMedia)
}