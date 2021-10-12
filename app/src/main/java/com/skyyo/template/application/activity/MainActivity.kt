package com.skyyo.template.application.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.plusAssign
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.skyyo.template.R
import com.skyyo.template.application.Destination
import com.skyyo.template.application.persistance.DataStoreManager
import com.skyyo.template.application.persistance.room.AppDatabase
import com.skyyo.template.theme.TemplateTheme
import com.skyyo.template.utils.eventDispatchers.NavigationDispatcher
import com.skyyo.template.utils.eventDispatchers.UnauthorizedEventDispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var navigationDispatcher: NavigationDispatcher

    @Inject
    lateinit var unauthorizedEventDispatcher: UnauthorizedEventDispatcher

    @Inject
    lateinit var appDatabase: AppDatabase

    @Suppress("RestrictedApi")
    @OptIn(
        ExperimentalMaterialApi::class,
        ExperimentalMaterialNavigationApi::class,
        ExperimentalAnimationApi::class
    )
    // simple core
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lockIntoPortrait()
        applyEdgeToEdge()
        setTheme(R.style.ThemeTemplate)

        val startDestination = when {
            runBlocking { dataStoreManager.getAccessToken() } == null -> Destination.SignIn.route
            else -> Destination.Tab1.route
        }

        setContent {
            val lifecycleOwner = LocalLifecycleOwner.current
            val systemUiController = rememberSystemUiController()
            val navController = rememberAnimatedNavController()

            val bottomSheetNavigator = rememberBottomSheetNavigator()
            navController.navigatorProvider += bottomSheetNavigator

            val navigationEvents = remember(navigationDispatcher.emitter, lifecycleOwner) {
                navigationDispatcher.emitter.receiveAsFlow().flowWithLifecycle(
                    lifecycleOwner.lifecycle,
                    Lifecycle.State.STARTED
                )
            }
            val unauthorizedEvents = remember(unauthorizedEventDispatcher.emitter, lifecycleOwner) {
                unauthorizedEventDispatcher.emitter.receiveAsFlow().flowWithLifecycle(
                    lifecycleOwner.lifecycle,
                    Lifecycle.State.STARTED
                )
            }

            LaunchedEffect(Unit) {
                launch {
                    navigationEvents.collect { event -> event(navController) }
                }
                launch {
                    unauthorizedEvents.collect { onUnauthorizedEventReceived() }
                }
            }

            DisposableEffect(navController) {
                val callback = NavController.OnDestinationChangedListener { _, destination, _ ->
                    when (destination.route) {
                        Destination.SignIn.route -> {
                            systemUiController.statusBarDarkContentEnabled = false
                        }
                        else -> {
                            systemUiController.statusBarDarkContentEnabled = true
                        }
                    }
                }
                navController.addOnDestinationChangedListener(callback)
                onDispose {
                    navController.removeOnDestinationChangedListener(callback)
                }
            }

            TemplateTheme {
                ProvideWindowInsets {
                    // used only for the bottom sheet destinations
                    ModalBottomSheetLayout(bottomSheetNavigator) {
                        PopulatedNavHost(
                            startDestination = startDestination,
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    // bottom bar core
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        lockIntoPortrait()
//        applyEdgeToEdge()
//        setTheme(R.style.ThemeTemplate)
//
//        val bottomBarScreens = listOf(
//            Destination.Tab1,
//            Destination.Tab2,
//            Destination.Tab3,
//        )
//        val startDestination = when {
//            runBlocking { dataStoreManager.getAccessToken() } == null -> Destination.SignIn.route
//            else -> Destination.Tab1.route
//        }
//
//        setContent {
//            val lifecycleOwner = LocalLifecycleOwner.current
//            val systemUiController = rememberSystemUiController()
//            val navController = rememberAnimatedNavController()
//
//            val bottomSheetNavigator = rememberBottomSheetNavigator()
//            navController.navigatorProvider += bottomSheetNavigator
//
//            val navigationEvents = remember(navigationDispatcher.emitter, lifecycleOwner) {
//                navigationDispatcher.emitter.receiveAsFlow().flowWithLifecycle(
//                    lifecycleOwner.lifecycle,
//                    Lifecycle.State.STARTED
//                )
//            }
//            val unauthorizedEvents = remember(unauthorizedEventDispatcher.emitter, lifecycleOwner) {
//                unauthorizedEventDispatcher.emitter.receiveAsFlow().flowWithLifecycle(
//                    lifecycleOwner.lifecycle,
//                    Lifecycle.State.STARTED
//                )
//            }
//
//            val isBottomBarVisible = rememberSaveable { mutableStateOf(false) }
//            val selectedTab = rememberSaveable { mutableStateOf(0) }
//
//            LaunchedEffect(Unit) {
//                launch {
//                    navigationEvents.collect { event -> event(navController) }
//                }
//                launch {
//                    unauthorizedEvents.collect { onUnauthorizedEventReceived() }
//                }
//            }
//
//            DisposableEffect(Unit) {
//                val callback = NavController.OnDestinationChangedListener { _, destination, _ ->
//                    when (destination.route) {
//                        Destination.SignIn.route -> {
//                            systemUiController.statusBarDarkContentEnabled = false
//                            isBottomBarVisible.value = false
//                        }
//                        else -> {
//                            isBottomBarVisible.value = true
//                        }
//                    }
//                }
//                navController.addOnDestinationChangedListener(callback)
//                onDispose {
//                    navController.removeOnDestinationChangedListener(callback)
//                }
//            }
//
//            TemplateTheme {
//                ProvideWindowInsets {
//                    // used only for the bottom sheet destinations
//                    ModalBottomSheetLayout(bottomSheetNavigator) {
//                        Box {
//                            PopulatedNavHost(
//                                startDestination = startDestination,
//                                navController = navController,
//                                onBackPressIntercepted = {
//                                    selectedTab.value = 0
//                                    navController.navigateToRootDestination(Destination.Tab1.route)
//                                }
//                            )
//                            AnimatedBottomBar(
//                                Modifier.align(Alignment.BottomCenter),
//                                bottomBarScreens,
//                                selectedTab.value,
//                                isBottomBarVisible.value
//                            ) { index, route ->
//                                // this means we're already on the selected tab
//                                if (index == selectedTab.value) return@AnimatedBottomBar
//                                selectedTab.value = index
//                                navController.navigateToRootDestination(route)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    // drawer core
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        lockIntoPortrait()
//
//        applyEdgeToEdge()
//        setTheme(R.style.ThemeTemplate)
//
//        val drawerScreens = listOf(
//            Destination.Tab1,
//            Destination.Tab2,
//            Destination.Tab3,
//        )
//        val startDestination = when {
//            runBlocking { dataStoreManager.getAccessToken() } == null -> Destination.SignIn.route
//            else -> Destination.Tab1.route
//        }
//
//        setContent {
//            val lifecycleOwner = LocalLifecycleOwner.current
//            val systemUiController = rememberSystemUiController()
//            val navController = rememberAnimatedNavController()
//
//            val bottomSheetNavigator = rememberBottomSheetNavigator()
//            navController.navigatorProvider += bottomSheetNavigator
//
//            val navigationEvents = remember(navigationDispatcher.emitter, lifecycleOwner) {
//                navigationDispatcher.emitter.receiveAsFlow().flowWithLifecycle(
//                    lifecycleOwner.lifecycle,
//                    Lifecycle.State.STARTED
//                )
//            }
//            val unauthorizedEvents = remember(unauthorizedEventDispatcher.emitter, lifecycleOwner) {
//                unauthorizedEventDispatcher.emitter.receiveAsFlow().flowWithLifecycle(
//                    lifecycleOwner.lifecycle,
//                    Lifecycle.State.STARTED
//                )
//            }
//
//            val isDrawerVisible = rememberSaveable { mutableStateOf(false) }
//            val selectedTab = rememberSaveable { mutableStateOf(0) }
//            val scaffoldState = rememberScaffoldState()
//            val scope = rememberCoroutineScope()
//
//            LaunchedEffect(Unit) {
//                launch {
//                    navigationEvents.collect { event -> event(navController) }
//                }
//                launch {
//                    unauthorizedEvents.collect { onUnauthorizedEventReceived() }
//                }
//            }
//
//            DisposableEffect(Unit) {
//                val callback = NavController.OnDestinationChangedListener { _, destination, _ ->
//                    when (destination.route) {
//                        Destination.SignIn.route -> {
//                            systemUiController.statusBarDarkContentEnabled = false
//                            isDrawerVisible.value = false
//                        }
//                        else -> {
//                            systemUiController.statusBarDarkContentEnabled = true
//                            isDrawerVisible.value = true
//                        }
//                    }
//                }
//                navController.addOnDestinationChangedListener(callback)
//                onDispose {
//                    navController.removeOnDestinationChangedListener(callback)
//                }
//            }
//
//            TemplateTheme {
//                ProvideWindowInsets {
//                    // used only for the bottom sheet destinations
//                    ModalBottomSheetLayout(bottomSheetNavigator) {
//                        val animationSpec = remember { tween<Float>(500) }
//                        Scaffold(
//                            scaffoldState = scaffoldState,
//                            // this allows to dismiss the drawer if its open by tapping on dimmed area
//                            drawerGesturesEnabled = scaffoldState.drawerState.let { it.isOpen && !it.isAnimationRunning },
//                            floatingActionButton = {
//                                if (isDrawerVisible.value) {
//                                    FloatingActionButton(onClick = {
//                                        scope.launch {
//                                            scaffoldState.drawerState.animateTo(
//                                                DrawerValue.Open,
//                                                animationSpec
//                                            )
//                                        }
//                                    }) {
//                                        Text(text = "open drawer")
//                                    }
//                                }
//                            },
//                            drawerContent = {
//                                if (isDrawerVisible.value) {
//                                    Drawer(
//                                        screens = drawerScreens,
//                                        selectedTab = selectedTab.value
//                                    ) { index, route ->
//                                        // this means we're already on the selected tab
//                                        if (index != selectedTab.value) {
//                                            selectedTab.value = index
//                                            navController.navigateToRootDestination(route)
//                                        }
//                                        // Skip closing drawer if it's not opened completely
//                                        if (scaffoldState.drawerState.let { it.isClosed && it.isAnimationRunning }) return@Drawer
//                                        scope.launch {
//                                            scaffoldState.drawerState.animateTo(
//                                                DrawerValue.Closed,
//                                                animationSpec
//                                            )
//                                        }
//                                    }
//                                }
//                            },
//                            content = {
//                                PopulatedNavHost(
//                                    startDestination = startDestination,
//                                    navController = navController,
//                                    onBackPressIntercepted = {
//                                        selectedTab.value = 0
//                                        navController.navigateToRootDestination(Destination.Tab1.route)
//                                    }
//                                )
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    }

    @Suppress("GlobalCoroutineUsage")
    @OptIn(DelicateCoroutinesApi::class)
    private fun onUnauthorizedEventReceived() {
        if (isFinishing) return
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.clearAllTables()
            dataStoreManager.clearData()
        }
        finish()
        startActivity(intent)
    }

    private fun applyEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun lockIntoPortrait() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
