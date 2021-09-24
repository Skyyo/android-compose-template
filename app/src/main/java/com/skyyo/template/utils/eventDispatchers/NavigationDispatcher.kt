package com.skyyo.template.utils.eventDispatchers

import androidx.navigation.NavController
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

typealias NavigationEvent = (NavController) -> Unit

@ActivityRetainedScoped
class NavigationDispatcher @Inject constructor() {
    val emitter = Channel<NavigationEvent>(Channel.UNLIMITED)

    fun emit(navigationEvent: NavigationEvent) = emitter.trySend(navigationEvent)
}
