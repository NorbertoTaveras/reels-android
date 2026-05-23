package com.norbertotaveras.reels.domain.model.settings

enum class AppTheme(val label: String) {
    System(label = "Use system setting"),
    Dark(label = "Dark"),
    Light(label = "Light");

    fun next(): AppTheme {
        return when (this) {
            System -> Dark
            Dark -> Light
            Light -> System
        }
    }
}
