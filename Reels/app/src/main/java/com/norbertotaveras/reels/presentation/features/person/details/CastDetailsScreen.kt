/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.person.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CastDetailsScreen(
    state: CastDetailsUiState,
    onEvent: (CastDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    CastDetailsStateContent(
        state = state,
        onEvent = onEvent,
        modifier = modifier
    )
}
