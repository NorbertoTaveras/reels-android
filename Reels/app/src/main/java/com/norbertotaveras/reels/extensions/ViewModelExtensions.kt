package com.norbertotaveras.reels.extensions

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