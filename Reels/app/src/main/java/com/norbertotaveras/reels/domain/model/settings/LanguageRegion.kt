package com.norbertotaveras.reels.domain.model.settings

enum class LanguageRegion(
    val languageCode: String,
    val label: String,
) {
    EnglishUnitedStates(
        languageCode = "en-US",
        label = "English · United States"
    ),
    SpanishUnitedStates(
        languageCode = "es-US",
        label = "Spanish · United States"
    );

    fun next(): LanguageRegion {
        return when (this) {
            EnglishUnitedStates -> SpanishUnitedStates
            SpanishUnitedStates -> EnglishUnitedStates
        }
    }
}
