/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.settings

enum class LanguageRegion(
    val languageCode: String,
) {
    EnglishUnitedStates(languageCode = "en-US"),
    SpanishSpain(languageCode = "es-ES"),
    FrenchFrance(languageCode = "fr-FR"),
    JapaneseJapan(languageCode = "ja-JP");

    companion object {
        val Default = EnglishUnitedStates
    }

    fun next(): LanguageRegion {
        return when (this) {
            EnglishUnitedStates -> SpanishSpain
            SpanishSpain -> FrenchFrance
            FrenchFrance -> JapaneseJapan
            JapaneseJapan -> EnglishUnitedStates
        }
    }
}
