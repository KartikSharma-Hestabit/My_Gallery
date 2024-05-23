package com.example.mygallery.repos

import android.media.ThumbnailUtils
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import com.example.mygallery.models.FileModel
import com.example.mygallery.models.GalleryModel
import java.io.File
import javax.inject.Inject

class GalleryRepoImpl @Inject constructor() : GalleryRepo {

    override suspend fun getDCIMImages(): Resource<List<GalleryModel>> {
        return try {
            var dcimPath = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "Camera".toString()
            )

            // If Camera directory doesn't exist within DCIM, fall back to DCIM
            if (!dcimPath.exists() || !dcimPath.isDirectory) {
                dcimPath = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        .toString()
                )
            }

            // Retrieve list of files from the chosen directory
            val fileList =
                dcimPath.listFiles()?.toList()!!

            Log.d("DCIM", "getDCIMImages: ${fileList.size}")

            val fileModelList = arrayListOf<FileModel>()

            DataManager.allGalleryFiles = arrayListOf()

            for (file in fileList.indices) {
                DataManager.allGalleryFiles.add(
                    FileModel(
                        id = file,
                        DataManager.dateFormat(fileList[file].lastModified()),
                        fileList[file].absolutePath,
                        fileList[file].extension
                    )
                )
                fileModelList.add(
                    FileModel(
                        id = file,
                        DataManager.dateFormat(fileList[file].lastModified()),
                        fileList[file].absolutePath,
                        fileList[file].extension
                    )
                )

                Log.d("DCIM", "getDCIMImages: ${fileList[file].absolutePath}")

            }

            DataManager.allGalleryFiles.sortByDescending { fileModel -> fileModel.date }

            fileModelList.sortByDescending { fileModel -> fileModel.date }

            val galleryModelList = arrayListOf<GalleryModel>()

            var file = 0
            while (file < fileModelList.size) {

                val tempDate = fileModelList[file].date
                val datedList = arrayListOf<FileModel>()

                while (file < fileModelList.size &&
                    tempDate == fileModelList[file].date
                ) {
                    datedList.add(fileModelList[file])
                    file++
                }

                galleryModelList.add(GalleryModel(tempDate, datedList))

            }

            Resource.Success(galleryModelList)
        } catch (e: Exception) {
            // Log any exceptions for debugging
            Log.e("DCIM Error", "Error while retrieving DCIM images", e)
            // Return failure resource with the caught exception
            Resource.Failure(e)
        }
    }
}
