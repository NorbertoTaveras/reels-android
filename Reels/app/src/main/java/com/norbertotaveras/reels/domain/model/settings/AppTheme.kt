/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.settings

enum class AppTheme {
    System,
    Dark,
    Light;

    fun next(): AppTheme {
        return when (this) {
            System -> Dark
            Dark -> Light
            Light -> System
        }
    }
}
