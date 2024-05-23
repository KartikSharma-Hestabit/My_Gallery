package com.example.mygallery.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygallery.models.FileModel
import com.example.mygallery.models.GalleryModel
import com.example.mygallery.repos.GalleryRepo
import com.example.mygallery.repos.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(private val galleryRepo: GalleryRepo) :
    ViewModel() {

    private val _filesFlow = MutableStateFlow<Resource<List<GalleryModel>>?>(null)
    val filesFlow: StateFlow<Resource<List<GalleryModel>>?> = _filesFlow

    private fun getDCIMFiles() = viewModelScope.launch {

        _filesFlow.value = Resource.Loading
        val result = galleryRepo.getDCIMImages()
        _filesFlow.value = result

    }

    init {
        getDCIMFiles()
    }

}