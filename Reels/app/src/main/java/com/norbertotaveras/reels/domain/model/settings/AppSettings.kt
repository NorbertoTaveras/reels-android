package com.norbertotaveras.reels.domain.model.settings

data class AppSettings(
    val imageQuality: ImageQuality = ImageQuality.High,
    val languageRegion: LanguageRegion = LanguageRegion.EnglishUnitedStates,
    val theme: AppTheme = AppTheme.System,
)
