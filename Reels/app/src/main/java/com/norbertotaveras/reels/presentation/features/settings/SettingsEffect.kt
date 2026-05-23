package com.norbertotaveras.reels.presentation.features.settings

sealed interface SettingsEffect {
    data object NavigateToFavorites : SettingsEffect
    data object ClearImageCache : SettingsEffect
    data class OpenExternalUrl(val url: String) : SettingsEffect
}
