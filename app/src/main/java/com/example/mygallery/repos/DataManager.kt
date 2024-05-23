package com.example.mygallery.repos

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.mygallery.models.FileModel
import java.net.URLConnection
import java.text.SimpleDateFormat


object DataManager {

    var allGalleryFiles: ArrayList<FileModel> = arrayListOf<FileModel>()

    fun dateFormat(temp: Any): String {
        val simpleDate = SimpleDateFormat("dd/MMMM/yyyy")
        return simpleDate.format(temp)
    }

    fun isImageFile(path: String?): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.startsWith("image")
    }

    fun createBitmapFromUri(path: String): ImageBitmap {
        return ThumbnailUtils.createVideoThumbnail(
            path,
            MediaStore.Video.Thumbnails.MICRO_KIND
        )!!.asImageBitmap()
    }

}