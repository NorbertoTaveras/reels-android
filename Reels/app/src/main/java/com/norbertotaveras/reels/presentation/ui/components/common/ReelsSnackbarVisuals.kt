/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

data class ReelsSnackbarVisuals(
    override val message: String,
    val title: String? = null,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = true,
    override val duration: SnackbarDuration = SnackbarDuration.Long,
) : SnackbarVisuals
