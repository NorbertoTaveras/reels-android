/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.norbertotaveras.reels.domain.settings.model.ImageQuality

val LocalImageQuality = staticCompositionLocalOf { ImageQuality.High }
