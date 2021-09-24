package com.skyyo.template.utils.eventDispatchers

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

@ActivityRetainedScoped
class UnauthorizedEventDispatcher @Inject constructor() {
    val emitter = Channel<Boolean>()

    suspend fun requestDeauthorization() {
        emitter.send(true)
    }
}
