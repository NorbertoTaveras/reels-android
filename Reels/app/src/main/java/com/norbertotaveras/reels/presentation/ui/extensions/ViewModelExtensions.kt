/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun ViewModel.launchOnMainDispatcher(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(Dispatchers.Main, block = block)
}

fun ViewModel.launchOnIODispatcher(block: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch(Dispatchers.IO, block = block)
}

fun ViewModel.launchOnDefaultDispatcher(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(Dispatchers.Default, block = block)
}