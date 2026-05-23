package com.norbertotaveras.reels.presentation.features.settings

data class SettingsUiState(
    val imageQualityLabel: String = "High quality posters and backdrops",
    val languageRegionLabel: String = "English · United States",
    val themeLabel: String = "Use system setting",
    val savedMoviesLabel: String = "Manage movies saved on this device",
    val clearImageCacheLabel: String = "Free up locally cached poster images",
    val appVersionLabel: String = "1.0.0",
    val privacyLabel: String = "Local favorites stay on this device",
)
