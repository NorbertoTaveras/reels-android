/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel

@Composable
fun MovieCategoryScreen(
    state: MovieCategoryUiState,
    movies: LazyPagingItems<MovieDataModel>,
    onEvent: (MovieCategoryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    MovieCategoryContent(
        state = state,
        movies = movies,
        onEvent = onEvent,
        modifier = modifier
    )
}
