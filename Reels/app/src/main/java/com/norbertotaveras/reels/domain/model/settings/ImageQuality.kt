package com.norbertotaveras.reels.domain.model.settings

enum class ImageQuality(
    val label: String,
    val tmdbSizePath: String,
) {
    High(
        label = "High quality posters and backdrops",
        tmdbSizePath = "w780"
    ),
    Standard(
        label = "Standard quality posters and backdrops",
        tmdbSizePath = "w342"
    );

    fun next(): ImageQuality {
        return when (this) {
            High -> Standard
            Standard -> High
        }
    }
}
