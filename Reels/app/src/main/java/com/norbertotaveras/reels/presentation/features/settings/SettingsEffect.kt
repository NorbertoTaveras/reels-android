package com.norbertotaveras.reels.presentation.features.settings

sealed interface SettingsEffect {
    data class OpenExternalUrl(val url: String) : SettingsEffect
}
