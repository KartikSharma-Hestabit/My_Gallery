package com.example.mygallery.screens

import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import com.example.mygallery.models.FileModel
import com.example.mygallery.models.GalleryModel
import com.example.mygallery.repos.DataManager
import com.example.mygallery.repos.Resource
import com.example.mygallery.ui.theme.DarkBlue
import com.example.mygallery.viewModels.GalleryViewModel
import java.io.File
import java.util.Calendar
import java.util.Date
import kotlin.math.log


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(onItemClicked: (id: Int) -> Unit) {

    val galleryVM: GalleryViewModel = hiltViewModel()
    val filesFlow = galleryVM.filesFlow.collectAsState()

    var files by remember {
        mutableStateOf(emptyList<GalleryModel>())
    }

    filesFlow.value?.let {
        when (it) {
            is Resource.Failure -> {
                Log.d("DCIM response", "GalleryScreen: Failure ${it.exception.message}")
            }

            Resource.Loading -> {}
            is Resource.Success -> {

                files = it.result
            }
        }
    }

    val cal = Calendar.getInstance()
    cal.add(Calendar.DATE, -1);



    LazyColumn {

        items(count = files.size) { dateFile ->

            Text(
                text = if (files[dateFile].date == DataManager.dateFormat(Date())) {
                    "Today"
                } else if (files[dateFile].date == DataManager.dateFormat(
                        cal.time
                    )
                ) {
                    "Yesterday"
                } else {
                    files[dateFile].date
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = DarkBlue())
                    .padding(horizontal = 16.dp, vertical = 15.dp)
            )

            CustomGrid(
                files = files[dateFile].fileList,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                onItemClicked(it)
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomGrid(
    files: List<FileModel>,
    modifier: Modifier = Modifier,
    onItemClicked: (id: Int) -> Unit
) {

    FlowRow(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp),
    ) {
        repeat(files.size) { fileIndex ->

            Box(modifier = Modifier.clickable {
                onItemClicked(files[fileIndex].id)
                Log.d("MediaId", "CustomGrid: ${files[fileIndex].id}")
            }) {

                LazyRow {
                    item {
                        Box {
                            if (files[fileIndex].extension == "mp4") {
                                Log.d(
                                    "DCIM response",
                                    "CustomGrid: Video - ${files[fileIndex].file}"
                                )

                                val imageLoader = ImageLoader.Builder(LocalContext.current)
                                    .components {
                                        add(VideoFrameDecoder.Factory())
                                    }
                                    .build()

                                Box(
                                    contentAlignment = Alignment.TopStart,
                                ) {

                                    AsyncImage(
                                        model = files[fileIndex].file,//your video here
                                        imageLoader = imageLoader,
                                        contentDescription = "icon",
                                        modifier = Modifier
                                            .padding(2.dp)
                                            .fillParentMaxWidth(0.32f)
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(4.dp)),
                                        contentScale = ContentScale.Crop
                                    )

                                    Icon(
                                        imageVector = Icons.Default.PlayCircle,
                                        contentDescription = "Video Icon",
                                        modifier = Modifier.padding(5.dp),
                                        tint = Color.White
                                    )
                                }
                            } else {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(files[fileIndex].file)
                                        .build(),
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .fillParentMaxWidth(0.32f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                }


            }
        }
    }
}
