/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

sealed interface SettingsEffect {
    data object NavigateToLibrary : SettingsEffect
    data object ClearImageCache : SettingsEffect
    data class OpenExternalUrl(val url: String) : SettingsEffect
}
