package com.skyyo.template.utils.extensions

import android.os.Bundle
import androidx.lifecycle.asFlow
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.skyyo.template.application.Destination

// sets value to previous savedStateHandle unless route is specified
fun <T> NavController.setNavigationResult(route: String? = null, key: String, result: T) {
    if (route == null) {
        previousBackStackEntry?.savedStateHandle?.set(key, result)
    } else {
        getBackStackEntry(route).savedStateHandle.set(key, result)
    }
}

fun <T> NavController.getNavigationResult(key: String) =
    currentBackStackEntry?.savedStateHandle?.get<T>(key)

fun <T> NavController.observeNavigationResultLiveData(key: String) =
    currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T> NavController.observeNavigationResult(key: String) =
    currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)?.asFlow()

// popUpToRoute - should always be the start destination of the bottomBar, not app
fun NavController.navigateToRootDestination(
    route: String,
    popUpToRoute: String = Destination.Tab1.route
) {
    navigate(route) {
        popUpTo(popUpToRoute) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateWithObject(
    route: String,
    navOptions: NavOptions? = null,
    extras: Navigator.Extras? = null,
    arguments: Bundle? = null
) {
    navigate(route, navOptions, extras)
    if (arguments == null || arguments.isEmpty) return
    currentBackStackEntry?.arguments?.putAll(arguments)
}
