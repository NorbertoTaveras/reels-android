/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.filmography

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FilmographyScreen(
    state: FilmographyUiState,
    onEvent: (FilmographyEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    FilmographyContent(
        state = state,
        onEvent = onEvent,
        modifier = modifier
    )
}
