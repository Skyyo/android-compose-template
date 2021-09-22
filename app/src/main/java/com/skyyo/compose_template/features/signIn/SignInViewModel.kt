package com.skyyo.compose_template.features.signIn

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.compose_template.application.Destination
import com.skyyo.compose_template.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    fun goHome() = navigationDispatcher.emit {
        it.navigate(Destination.Tab1.route) {
            popUpTo(Destination.SignIn.route) {
                inclusive = true
            }
        }
    }
}
