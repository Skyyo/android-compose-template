package com.skyyo.compose_template.application.activity.cores.simple

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.skyyo.compose_template.application.Destination
import com.skyyo.compose_template.application.activity.PopulatedNavHost

@Composable
fun SimpleCore(
    startDestination: String,
    navController: NavHostController,
    systemUiController: SystemUiController
) {
    DisposableEffect(navController) {
        val callback = NavController.OnDestinationChangedListener { _, destination, args ->
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

    Scaffold(
        content = { innerPadding ->
            PopulatedNavHost(
                startDestination = startDestination,
                innerPadding = innerPadding,
                navController = navController
            )
        })
}