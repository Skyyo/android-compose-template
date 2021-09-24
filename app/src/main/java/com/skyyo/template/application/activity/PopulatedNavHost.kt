package com.skyyo.template.application.activity

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.pager.ExperimentalPagerApi
import com.skyyo.template.application.Destination
import com.skyyo.template.features.signIn.SignInScreen
import com.skyyo.template.features.tab1.Tab1Screen
import com.skyyo.template.features.tab2.Tab2Screen
import com.skyyo.template.features.tab3.Tab3Screen

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterialNavigationApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalPagerApi::class
)
@Composable
fun PopulatedNavHost(
    startDestination: String,
    innerPadding: PaddingValues,
    navController: NavHostController,
    onBackPressIntercepted: (() -> Unit)? = null
) = AnimatedNavHost(
    navController = navController,
    startDestination = startDestination,
    modifier = Modifier.padding(innerPadding)
) {
    composable(Destination.SignIn.route) { SignInScreen() }

    composable(Destination.Tab1.route) { Tab1Screen() }
    composable(Destination.Tab2.route) {
        onBackPressIntercepted?.let { BackHandler(onBack = it) }
        Tab2Screen()
    }
    composable(Destination.Tab3.route) {
        onBackPressIntercepted?.let { BackHandler(onBack = it) }
        Tab3Screen()
    }
}