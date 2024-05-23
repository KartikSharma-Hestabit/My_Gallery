package com.example.mygallery.repos

import com.example.mygallery.models.FileModel
import com.example.mygallery.models.GalleryModel

interface GalleryRepo {
    suspend fun getDCIMImages(): Resource<List<GalleryModel>>
}