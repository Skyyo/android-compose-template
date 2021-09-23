package com.skyyo.compose_template.application.activity.cores.drawer

import androidx.compose.animation.core.tween
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.skyyo.compose_template.application.Destination
import com.skyyo.compose_template.application.activity.PopulatedNavHost
import com.skyyo.compose_template.extensions.navigateToRootDestination
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@Composable
fun DrawerCore(
    drawerScreens: List<Destination>,
    startDestination: String,
    navController: NavHostController,
    systemUiController: SystemUiController
) {
    val isDrawerVisible = rememberSaveable { mutableStateOf(false) }
    val selectedTab = rememberSaveable { mutableStateOf(0) }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val animationSpec = remember { tween<Float>(500) }

    DisposableEffect(Unit) {
        val callback = NavController.OnDestinationChangedListener { _, destination, args ->
            when (destination.route) {
                Destination.SignIn.route -> {
                    systemUiController.statusBarDarkContentEnabled = false
                    isDrawerVisible.value = false
                }
                else -> {
                    systemUiController.statusBarDarkContentEnabled = true
                    isDrawerVisible.value = true
                }
            }
        }
        navController.addOnDestinationChangedListener(callback)
        onDispose {
            navController.removeOnDestinationChangedListener(callback)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        //this allows to dismiss the drawer if its open by tapping on dimmed area
        drawerGesturesEnabled = scaffoldState.drawerState.let { it.isOpen && !it.isAnimationRunning },
        floatingActionButton = {
            if (isDrawerVisible.value) {
                FloatingActionButton(onClick = {
                    scope.launch {
                        scaffoldState.drawerState.animateTo(
                            DrawerValue.Open,
                            animationSpec
                        )
                    }
                }) {
                    Text(text = "open drawer")
                }
            }
        },
        drawerContent = {
            if (isDrawerVisible.value) {
                Drawer(
                    screens = drawerScreens,
                    selectedTab = selectedTab.value
                ) { index, route ->
                    //this means we're already on the selected tab
                    if (index != selectedTab.value) {
                        selectedTab.value = index
                        navController.navigateToRootDestination(route)
                    }
                    //Skip closing drawer if it's not opened completely
                    if (scaffoldState.drawerState.let { it.isClosed && it.isAnimationRunning }) return@Drawer
                    scope.launch {
                        scaffoldState.drawerState.animateTo(
                            DrawerValue.Closed,
                            animationSpec
                        )
                    }
                }
            }
        },
        content = { innerPadding ->
            PopulatedNavHost(
                startDestination = startDestination,
                innerPadding = innerPadding,
                navController = navController,
                onBackPressIntercepted = {
                    selectedTab.value = 0
                    navController.navigateToRootDestination(Destination.Tab1.route)
                }
            )
        }
    )
}


