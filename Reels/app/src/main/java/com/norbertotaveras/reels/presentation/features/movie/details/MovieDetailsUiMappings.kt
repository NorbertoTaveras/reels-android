/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.details

import androidx.compose.runtime.Composable
import com.norbertotaveras.reels.domain.account.model.TmdbMovieAccountStateDataModel
import com.norbertotaveras.reels.domain.movie.model.MovieDetailsContentDataModel
import com.norbertotaveras.reels.presentation.ui.components.movie.PosterCarouselItem
import com.norbertotaveras.reels.presentation.ui.components.trailer.TrailerCarouselItem
import com.norbertotaveras.reels.presentation.ui.extensions.toReleaseYear
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl

internal fun MovieDetailsUiState.toTrailerCarouselItems(): List<TrailerCarouselItem> {
    return trailers.map { trailer ->
        TrailerCarouselItem(
            id = trailer.id,
            title = trailer.name,
            thumbnailUrl = trailer.youtubeThumbnailUrl,
            source = trailer.site,
            duration = null
        )
    }
}

@Composable
internal fun MovieDetailsUiState.toSimilarMovieCarouselItems(): List<PosterCarouselItem> {
    return similarMovies.map { movie ->
        PosterCarouselItem(
            id = movie.id,
            title = movie.title,
            posterUrl = movie.posterPath.toTmdbImageUrl(),
            releaseYear = movie.releaseDate.toReleaseYear()
        )
    }
}

internal fun MovieDetailsUiState.withTmdbAccountState(
    accountState: TmdbMovieAccountStateDataModel?,
): MovieDetailsUiState {
    return copy(
        isWatchlisted = accountState?.isWatchlisted ?: false,
        tmdbRating = accountState?.rating
    )
}

internal fun MovieDetailsUiState.withContent(
    content: MovieDetailsContentDataModel,
): MovieDetailsUiState {
    return copy(
        movie = content.movie,
        cast = content.cast,
        trailers = content.trailers,
        similarMovies = content.similarMovies,
        isOfflineContent = content.isOfflineContent,
        cachedAt = content.cachedAt,
        errorMessage = null,
        isLoading = false
    )
}
