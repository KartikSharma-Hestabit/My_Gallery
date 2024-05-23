package com.example.mygallery.screens

import android.content.Context
import android.util.Log
import android.view.View
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import com.example.mygallery.R
import com.example.mygallery.models.FileModel
import com.example.mygallery.models.GalleryModel
import com.example.mygallery.repos.DataManager
import com.example.mygallery.repos.Resource
import com.example.mygallery.viewModels.GalleryViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaScreen(id: Int = 0) {

//    val galleryViewModel: GalleryViewModel = hiltViewModel()

    val mediaFiles = DataManager.allGalleryFiles

//    mediaFiles.reverse()

//    val pagerState = rememberPagerState(pageCount = { mediaFiles.size })


    val indicatorScrollState = rememberLazyListState()

    /*LaunchedEffect(Unit) {
        val size =
            indicatorScrollState.layoutInfo.visibleItemsInfo.last().index - indicatorScrollState.firstVisibleItemIndex

        val half = size / 2

        repeat(half) {
            mediaFiles.add(it, FileModel(101, "asdf", "asdf", "adsf"))
            mediaFiles.add(FileModel(101, "asdf", "asdf", "adsf"))
        }
//        mediaFiles.add(FileModel(101, "asdf", "asdf", "adsf"))

    }*/
    val pagerState = rememberPagerState(
        pageCount = { mediaFiles.size },
    )

    LaunchedEffect(key1 = pagerState.currentPage, block = {
        indicatorScrollState.animateScrollToItem(pagerState.currentPage)

        Log.d("MediaId", "pager currentPage - ${pagerState.currentPage}")
    })

    LaunchedEffect(
        key1 = indicatorScrollState.firstVisibleItemIndex,
        block = {
            pagerState.animateScrollToPage(indicatorScrollState.firstVisibleItemIndex)

            Log.d("MediaId", "indicator currentPage - ${pagerState.currentPage}")

        })
    LaunchedEffect(Unit) {

        pagerState.scrollToPage(id)
        indicatorScrollState.scrollToItem(id)
        Log.d("MediaId", "MediaScreen: $id, currentPage - ${pagerState.currentPage}")
    }

    Box(contentAlignment = Alignment.BottomCenter) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // Our page content

            if (mediaFiles[page].extension == "mp4") {
                Log.d("DCIM response", "CustomGrid: Video - ${mediaFiles[page].file}")

                val imageLoader = ImageLoader.Builder(LocalContext.current)
                    .components {
                        add(VideoFrameDecoder.Factory())
                    }
                    .build()

                var playVideo by remember {
                    mutableStateOf(false)
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 50.dp)
                        .clickable { playVideo = true }
                ) {


                    if (playVideo) {
                        ExoVideoPlayer(file = mediaFiles[page].file.toString())
                    } else {
                        AsyncImage(
                            model = mediaFiles[page].file,//your video here
                            imageLoader = imageLoader,
                            contentDescription = "icon",
                            modifier = Modifier
                                .padding(2.dp)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.FillWidth,
                        )

                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Video Icon",
                            modifier = Modifier
                                .padding(5.dp)
                                .size(40.dp)
                        )
                    }

                }
            } else {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(mediaFiles[page].file)
                        .build(),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.FillWidth,
                    contentDescription = ""
                )
            }
        }


        LazyRow(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(), // Only change is here I want to display 5 items here so 5 x (padding + size)
            horizontalArrangement = Arrangement.Center, state = indicatorScrollState,
            verticalAlignment = Alignment.CenterVertically
        ) {

            items(4) {
                Spacer(modifier = Modifier.fillParentMaxWidth(0.1f))
            }

            items(mediaFiles.size) { page ->

                val currentPage = pagerState.currentPage
                val firstVisibleIndex by remember { derivedStateOf { indicatorScrollState.firstVisibleItemIndex } }
                val lastVisibleIndex =
                    indicatorScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val size by animateDpAsState(
                    targetValue = if (page == currentPage) {
                        50.dp
                    } else {
                        40.dp
                    },
                    label = "dpAnimation",
                )

                if (mediaFiles[page].extension == "mp4") {
                    Log.d("DCIM response", "CustomGrid: Video - ${mediaFiles[page].file}")

                    val imageLoader = ImageLoader.Builder(LocalContext.current)
                        .components {
                            add(VideoFrameDecoder.Factory())
                        }
                        .build()

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {

                        AsyncImage(
                            model = mediaFiles[page].file,//your video here
                            imageLoader = imageLoader,
                            contentDescription = "icon",
                            modifier = Modifier
                                .padding(2.dp)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Crop,
                            alpha = if (pagerState.currentPage == page) 1f else 0.5f
                        )

                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Video Icon",
                            modifier = Modifier
                                .padding(5.dp)
                                .size(if (pagerState.currentPage == page) 20.dp else 10.dp)
                        )
                    }
                } else {

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(mediaFiles[page].file)
                            .build(),
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .size(size)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = "",
                        alpha = if (pagerState.currentPage == page) 1f else 0.5f
                    )
                }
            }
            items(5) {
                Spacer(modifier = Modifier.fillParentMaxWidth(0.1f))
            }
        }

        /*Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(16.dp)
                )
            }
        }*/


//        HorizontalDraggableSample(mediaFiles = mediaFiles)
    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalDraggableSample(
    modifier: Modifier = Modifier,
    mediaFiles: ArrayList<FileModel>
) {
    /*    val density = LocalDensity.current
        val state = remember {
            AnchoredDraggableState(
                initialValue = DragAnchors.Start,
                positionalThreshold = { distance: Float -> distance * 0.5f },
                velocityThreshold = { with(density) { 100.dp.toPx() } },
                animationSpec = tween(),
            ).apply {
                updateAnchors(
                    DraggableAnchors {
                        DragAnchors.Mid at -100f
                        DragAnchors.Start at 0f
                        DragAnchors.End at 100f
                    }
                )
            }
        }

        Box(
            modifier = modifier,
        ) {*/

    LazyRow {
        items(mediaFiles.size) { page ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(mediaFiles[page].file)
                    .build(),
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.FillWidth,
                contentDescription = ""
            )
        }
    }
}

@Composable
fun ExoVideoPlayer(file: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        getSimpleExoPlayer(context, file)
    }
    AndroidView(
        modifier = Modifier
            .fillMaxSize(),
        factory = { context1 ->
            PlayerView(context1).apply {
                player = exoPlayer
            }
        },
    )
}

private fun getSimpleExoPlayer(context: Context, file: String): ExoPlayer {
    return ExoPlayer.Builder(context).build().apply {
        val dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.packageName)
        )
        //local video
        val localVideoItem = MediaItem.fromUri(file)
        val localVideoSource = ProgressiveMediaSource
            .Factory(dataSourceFactory)
            .createMediaSource(localVideoItem)
        this.addMediaSource(localVideoSource)

        /* // streaming from internet
         val internetVideoItem = MediaItem.fromUri(VIDEO_URL)
         val internetVideoSource = ProgressiveMediaSource
             .Factory(dataSourceFactory)
             .createMediaSource(internetVideoItem)
         this.addMediaSource(internetVideoSource)*/
        // init
        this.prepare()
    }
}
