package com.example.mygallery.di

import com.example.mygallery.repos.GalleryRepo
import com.example.mygallery.repos.GalleryRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class Module {

    @Provides
    fun provideGalleryRepository(impl: GalleryRepoImpl): GalleryRepo = impl

}