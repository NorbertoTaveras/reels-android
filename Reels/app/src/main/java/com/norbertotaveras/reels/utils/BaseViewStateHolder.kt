package com.norbertotaveras.reels.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

abstract class BaseViewStateHolder(isLoading: Boolean) {
   var isLoading by mutableStateOf(isLoading)
   var error by mutableStateOf<String?>(null)
}