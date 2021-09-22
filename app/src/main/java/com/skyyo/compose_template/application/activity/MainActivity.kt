package com.skyyo.compose_template.application.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.plusAssign
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.skyyo.compose_template.application.Destination
import com.skyyo.compose_template.application.activity.cores.drawer.DrawerCore
import com.skyyo.compose_template.application.persistance.DataStoreManager
import com.skyyo.compose_template.application.persistance.room.AppDatabase
import com.skyyo.compose_template.theme.ComposetemplateTheme
import com.skyyo.compose_template.utils.eventDispatchers.NavigationDispatcher
import com.skyyo.compose_template.utils.eventDispatchers.UnauthorizedEventDispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
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

    @OptIn(ExperimentalMaterialApi::class)
    @ExperimentalMaterialNavigationApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyEdgeToEdge()
        //these boys won't be hoisted in the template
        val drawerOrBottomBarScreens = listOf(
            Destination.Tab1,
            Destination.Tab2,
            Destination.Tab3,
        )
        val startDestination = when {
            //TODO measure async + splash delegation profit
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
                navigationDispatcher.emitter.flowWithLifecycle(
                    lifecycleOwner.lifecycle,
                    Lifecycle.State.STARTED
                )
            }
            val unauthorizedEvents = remember(unauthorizedEventDispatcher.emitter, lifecycleOwner) {
                unauthorizedEventDispatcher.emitter.flowWithLifecycle(
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

            ComposetemplateTheme() {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    // used only for the bottom sheet destinations
                    ModalBottomSheetLayout(bottomSheetNavigator) {
//                        SimpleCore(
//                            startDestination,
//                            navController
//                        )
//                        BottomBarCore(
//                            drawerOrBottomBarScreens,
//                            startDestination,
//                            navController,
//                            systemUiController,
//                        )
                        DrawerCore(
                            drawerOrBottomBarScreens,
                            startDestination,
                            navController
                        )
                    }
                }
            }
        }
    }

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
}