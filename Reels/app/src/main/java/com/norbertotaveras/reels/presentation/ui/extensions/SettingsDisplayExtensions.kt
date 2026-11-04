/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.extensions

import com.norbertotaveras.reels.domain.settings.model.AppTheme
import com.norbertotaveras.reels.domain.settings.model.ImageQuality
import com.norbertotaveras.reels.domain.settings.model.LanguageRegion

val AppTheme.displayLabel: String
    get() = when (this) {
        AppTheme.System -> "System default"
        AppTheme.Dark -> "Dark"
        AppTheme.Light -> "Light"
    }

val ImageQuality.displayLabel: String
    get() = when (this) {
        ImageQuality.High -> "High quality"
        ImageQuality.Standard -> "Standard"
        ImageQuality.Low -> "Low — saves data"
    }

val LanguageRegion.displayLabel: String
    get() = when (this) {
        LanguageRegion.EnglishUnitedStates -> "English · United States"
        LanguageRegion.SpanishSpain -> "Español · España"
        LanguageRegion.FrenchFrance -> "Français · France"
        LanguageRegion.JapaneseJapan -> "日本語 · 日本"
    }
