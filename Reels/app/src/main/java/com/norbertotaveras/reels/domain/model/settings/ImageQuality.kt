package com.norbertotaveras.reels.domain.model.settings

enum class ImageQuality(val label: String) {
    High(label = "High quality posters and backdrops"),
    Standard(label = "Standard quality posters and backdrops");

    fun next(): ImageQuality {
        return when (this) {
            High -> Standard
            Standard -> High
        }
    }
}
