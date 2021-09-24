package com.skyyo.template.application.activity.cores.bottomBar

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.skyyo.template.application.Destination
import com.skyyo.template.application.activity.PopulatedNavHost
import com.skyyo.template.utils.extensions.navigateToRootDestination

@Composable
fun BottomBarCore(
    bottomBarScreens: List<Destination>,
    startDestination: String,
    navController: NavHostController,
    systemUiController: SystemUiController,
) {
    val isBottomBarVisible = rememberSaveable { mutableStateOf(false) }
    val selectedTab = rememberSaveable { mutableStateOf(0) }
    DisposableEffect(Unit) {
        val callback = NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.route) {
                Destination.SignIn.route -> {
                    systemUiController.statusBarDarkContentEnabled = false
                    isBottomBarVisible.value = false
                }
                else -> {
                    isBottomBarVisible.value = true
                }
            }
        }
        navController.addOnDestinationChangedListener(callback)
        onDispose {
            navController.removeOnDestinationChangedListener(callback)
        }
    }

    Box {
        PopulatedNavHost(
            startDestination = startDestination,
            navController = navController,
            onBackPressIntercepted = {
                selectedTab.value = 0
                navController.navigateToRootDestination(Destination.Tab1.route)
            }
        )
        AnimatedBottomBar(
            Modifier.align(Alignment.BottomCenter),
            bottomBarScreens,
            selectedTab.value,
            isBottomBarVisible.value
        ) { index, route ->
            // this means we're already on the selected tab
            if (index == selectedTab.value) return@AnimatedBottomBar
            selectedTab.value = index
            navController.navigateToRootDestination(route)
        }
    }
}
