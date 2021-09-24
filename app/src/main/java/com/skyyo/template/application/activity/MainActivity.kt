package com.skyyo.template.application.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
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
import com.skyyo.template.R
import com.skyyo.template.application.Destination
import com.skyyo.template.application.activity.cores.bottomBar.BottomBarCore
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lockIntoPortrait()

        applyEdgeToEdge()
        setTheme(R.style.ThemeTemplate)

        val drawerOrBottomBarScreens = listOf(
            Destination.Tab1,
            Destination.Tab2,
            Destination.Tab3,
        )
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

            TemplateTheme {
                ProvideWindowInsets {
                    // used only for the bottom sheet destinations
                    ModalBottomSheetLayout(bottomSheetNavigator) {
//                        SimpleCore(
//                            startDestination,
//                            navController,
//                            systemUiController
//                        )
                        BottomBarCore(
                            drawerOrBottomBarScreens,
                            startDestination,
                            navController,
                            systemUiController,
                        )
//                        DrawerCore(
//                            drawerOrBottomBarScreens,
//                            startDestination,
//                            navController,
//                            systemUiController)
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

    @SuppressLint("SourceLockedOrientationActivity")
    private fun lockIntoPortrait() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
