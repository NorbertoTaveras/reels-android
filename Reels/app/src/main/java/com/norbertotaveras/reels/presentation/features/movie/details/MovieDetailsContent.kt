/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.domain.movie.model.GenreDataModel
import com.norbertotaveras.reels.domain.movie.model.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.person.model.CastMemberDataModel
import com.norbertotaveras.reels.presentation.ui.components.cast.CastCarousel
import com.norbertotaveras.reels.presentation.ui.components.movie.MovieHeroHeader
import com.norbertotaveras.reels.presentation.ui.components.movie.PosterCarouselItem
import com.norbertotaveras.reels.presentation.ui.components.movie.SimilarMoviesSection
import com.norbertotaveras.reels.presentation.ui.components.trailer.TrailerCarousel
import com.norbertotaveras.reels.presentation.ui.components.trailer.TrailerCarouselItem
import com.norbertotaveras.reels.presentation.ui.extensions.TmdbImageType
import com.norbertotaveras.reels.presentation.ui.extensions.toReleaseYear
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme
import java.util.Locale

@Composable
fun MovieDetailsContent(
    movie: MovieDetailsDataModel,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    isFavoriteLoading: Boolean = false,
    isOfflineContent: Boolean = false,
    cachedAt: Long? = null,
    savedPersonIds: Set<Int> = emptySet(),
    savingPersonIds: Set<Int> = emptySet(),
    isTmdbAccountConnected: Boolean = false,
    isWatchlisted: Boolean = false,
    tmdbRating: Double? = null,
    isAccountActionLoading: Boolean = false,
    hasPlans: Boolean = false,
    cast: List<CastMemberDataModel> = emptyList(),
    trailers: List<TrailerCarouselItem> = emptyList(),
    similarMovies: List<PosterCarouselItem> = emptyList(),
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onRatingClick: () -> Unit,
    onPlanClick: () -> Unit,
    onOfflineRefreshClick: () -> Unit,
    onCastClick: (Int) -> Unit,
    onCastSaveClick: (Int) -> Unit,
    onCastSeeAllClick: () -> Unit,
    onTrailerClick: (String) -> Unit,
    onSimilarMovieClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = ReelsSpacing.xxLarge),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            MovieHeroHeader(
                title = movie.title,
                backdropUrl = (movie.backdropPath ?: movie.posterPath).toTmdbImageUrl(
                    imageType = TmdbImageType.Backdrop
                ),
                posterUrl = movie.posterPath.toTmdbImageUrl(),
                metadata = listOfNotNull(
                    movie.releaseDate.toReleaseYear(),
                    movie.runtime?.takeIf { it > 0 }?.let { "$it min" }
                ),
                rating = String.format(Locale.getDefault(), "%.1f", movie.voteAverage),
                genres = movie.genres.map { it.name },
                isFavorite = isFavorite,
                isFavoriteLoading = isFavoriteLoading,
                isWatchlisted = isWatchlisted,
                tmdbRating = tmdbRating,
                isAccountActionLoading = isAccountActionLoading,
                hasPlans = hasPlans,
                onBackClick = onBackClick,
                onFavoriteClick = onFavoriteClick,
                onWatchlistClick = onWatchlistClick,
                onRatingClick = onRatingClick,
                onPlanClick = onPlanClick,
                statusContent = if (isOfflineContent) {
                    {
                        CachedVersionNotice(
                            cachedAt = cachedAt,
                            onRefreshClick = onOfflineRefreshClick
                        )
                    }
                } else {
                    null
                }
            )
        }

        item {
            OverviewSection(
                overview = movie.overview.ifBlank { "No overview available yet." },
                modifier = Modifier.padding(top = ReelsDimensions.detailsOverviewTopSpacing)
            )
        }

        item {
            MovieInfoSection(
                releaseDate = movie.releaseDate,
                originalLanguage = movie.originalLanguage,
                popularityLabel = "%,d votes".format(Locale.getDefault(), movie.voteCount),
                modifier = Modifier.padding(top = ReelsDimensions.detailsSectionSpacing)
            )
        }

        val hasMissingOnlineExtras = !isOfflineContent &&
            (cast.isEmpty() || trailers.isEmpty() || similarMovies.isEmpty())
        val areAllOnlineExtrasMissing = !isOfflineContent &&
            cast.isEmpty() &&
            trailers.isEmpty() &&
            similarMovies.isEmpty()

        if (areAllOnlineExtrasMissing) {
            item {
                MovieDetailsInlineNotice(
                    modifier = Modifier.padding(top = ReelsDimensions.detailsSectionSpacing),
                    title = "Extras unavailable",
                    message = "Cast, trailers, and similar movies are not available for this title yet."
                )
            }
        } else if (hasMissingOnlineExtras && cast.isEmpty()) {
            item {
                MovieDetailsInlineNotice(
                    modifier = Modifier.padding(top = ReelsDimensions.detailsSectionSpacing),
                    title = "Cast unavailable",
                    message = "TMDB has not provided cast details for this title yet."
                )
            }
        } else if (cast.isNotEmpty()) {
            item {
                CastCarousel(
                    modifier = Modifier.padding(top = ReelsDimensions.detailsSectionSpacing),
                    cast = cast,
                    savedPersonIds = savedPersonIds,
                    savingPersonIds = savingPersonIds,
                    onCastClick = onCastClick,
                    onSaveClick = onCastSaveClick,
                    onSeeAllClick = onCastSeeAllClick
                )
            }
        }

        if (!areAllOnlineExtrasMissing && !isOfflineContent && trailers.isEmpty()) {
            item {
                MovieDetailsInlineNotice(
                    modifier = Modifier.padding(top = ReelsDimensions.detailsSectionSpacing),
                    title = "No trailers available",
                    message = "There are no playable YouTube trailers for this movie right now."
                )
            }
        } else if (trailers.isNotEmpty()) {
            item {
                TrailerCarousel(
                    modifier = Modifier.padding(top = ReelsDimensions.detailsSectionSpacing),
                    trailers = trailers,
                    onTrailerClick = onTrailerClick
                )
            }
        }

        if (!areAllOnlineExtrasMissing && !isOfflineContent && similarMovies.isEmpty()) {
            item {
                MovieDetailsInlineNotice(
                    modifier = Modifier.padding(top = ReelsDimensions.detailsSectionSpacing),
                    title = "No similar movies yet",
                    message = "TMDB has not returned recommendations for this movie."
                )
            }
        } else if (similarMovies.isNotEmpty()) {
            item {
                SimilarMoviesSection(
                    modifier = Modifier.padding(top = ReelsDimensions.detailsSectionSpacing),
                    movies = similarMovies,
                    onMovieClick = onSimilarMovieClick
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun MovieDetailsContentPreview() {
    ReelsTheme {
        MovieDetailsContent(
            movie = MovieDetailsDataModel(
                id = 1,
                title = "Dune: Part Two",
                overview = "Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family. Facing a choice between the love of his life and the fate of the universe, he must prevent a terrible future only he can foresee.",
                posterPath = null,
                backdropPath = null,
                releaseDate = "2024-03-01",
                runtime = 166,
                voteAverage = 8.7,
                originalLanguage = "English",
                popularity = 1248.0,
                voteCount = 1248,
                genres = listOf(
                    GenreDataModel(id = 1, name = "Sci-Fi"),
                    GenreDataModel(id = 2, name = "Adventure"),
                    GenreDataModel(id = 3, name = "Drama")
                )
            ),
            isTmdbAccountConnected = true,
            isWatchlisted = true,
            tmdbRating = 8.0,
            savedPersonIds = setOf(1),
            cast = previewCast,
            trailers = previewTrailers,
            similarMovies = previewSimilarMovies,
            onBackClick = {},
            onFavoriteClick = {},
            onWatchlistClick = {},
            onRatingClick = {},
            onPlanClick = {},
            onOfflineRefreshClick = {},
            onCastClick = {},
            onCastSaveClick = {},
            onCastSeeAllClick = {},
            onTrailerClick = {},
            onSimilarMovieClick = {}
        )
    }
}
