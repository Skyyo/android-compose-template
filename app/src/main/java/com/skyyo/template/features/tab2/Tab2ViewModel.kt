package com.skyyo.template.features.tab2

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.template.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class Tab2ViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel()
