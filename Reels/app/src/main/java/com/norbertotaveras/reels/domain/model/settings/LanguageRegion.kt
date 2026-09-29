/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.settings

enum class LanguageRegion(
    val languageCode: String,
    val label: String,
) {
    EnglishUnitedStates(
        languageCode = "en-US",
        label = "English · United States"
    ),
    SpanishSpain(
        languageCode = "es-ES",
        label = "Español · España"
    ),
    FrenchFrance(
        languageCode = "fr-FR",
        label = "Français · France"
    ),
    JapaneseJapan(
        languageCode = "ja-JP",
        label = "日本語 · 日本"
    );

    fun next(): LanguageRegion {
        return when (this) {
            EnglishUnitedStates -> SpanishSpain
            SpanishSpain -> FrenchFrance
            FrenchFrance -> JapaneseJapan
            JapaneseJapan -> EnglishUnitedStates
        }
    }
}
