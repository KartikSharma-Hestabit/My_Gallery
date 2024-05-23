package com.example.mygallery

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mygallery.screens.GalleryScreen
import com.example.mygallery.screens.MediaScreen
import com.example.mygallery.screens.Screen
import com.example.mygallery.ui.theme.BottomNavigationDarkColor
import com.example.mygallery.ui.theme.BottomNavigationLightColor
import com.example.mygallery.ui.theme.DarkBlue
import com.example.mygallery.ui.theme.MyGalleryTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyGalleryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationSetup()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationSetup() {

    val navController: NavHostController = rememberNavController()

    val items = listOf(
        Screen.Gallery,
        Screen.MediaScreen,
    )

    var appBarTitle by remember {
        mutableStateOf("Gallery")
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = appBarTitle,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            navigationIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Back Navigation",
                    modifier = Modifier.clickable {
                    }
                )
            },
            actions = {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = ""
                )
            })
    },
        bottomBar = {

            BottomNavigation(
                backgroundColor = if (isSystemInDarkTheme()) BottomNavigationDarkColor() else BottomNavigationLightColor(),
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(id = screen.resourceId),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 5.dp)
                            )
                        },
                        unselectedContentColor = Color.DarkGray,
                        selectedContentColor = Color.White,
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            appBarTitle = screen.route
                            navController.navigate(
                                if (screen.route == Screen.Gallery.route) screen.route else {
                                    "${screen.route}/0"
                                }
                            ) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }) {

        NavHost(
            navController = navController,
            startDestination = Screen.Gallery.route,
            modifier = Modifier.padding(paddingValues = it)
        ) {

            composable(route = Screen.Gallery.route) {
                appBarTitle = stringResource(id = Screen.Gallery.resourceId)
                GalleryScreen { id ->
                    navController.navigate(
                        ("${Screen.MediaScreen.route}/${id}")
                    ) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            }

            composable(route = "${Screen.MediaScreen.route}/{id}") { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id")
                appBarTitle = stringResource(id = Screen.MediaScreen.resourceId)
                if (id != null) {
                    MediaScreen(id.toInt())
                } else {
                    MediaScreen(0)
                }
            }

        }
    }
}
