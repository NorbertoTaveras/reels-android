package com.norbertotaveras.reels.presentation.features.movies.details

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.domain.model.cast.CastMemberDataModel
import com.norbertotaveras.reels.domain.model.genre.GenreDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.extensions.toDisplayTmdbDate
import com.norbertotaveras.reels.presentation.ui.components.cast.CastCarousel
import com.norbertotaveras.reels.presentation.ui.components.movie.MovieHeroHeader
import com.norbertotaveras.reels.presentation.ui.components.movie.PosterCarouselItem
import com.norbertotaveras.reels.presentation.ui.components.movie.SimilarMoviesSection
import com.norbertotaveras.reels.presentation.ui.components.trailer.TrailerCarousel
import com.norbertotaveras.reels.presentation.ui.components.trailer.TrailerCarouselItem
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsBorder
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme
import java.util.Locale

private data class MovieInfoRowData(
    val label: String,
    val value: String,
    val icon: MovieInfoIcon,
)

private enum class MovieInfoIcon {
    ReleaseDate,
    Language,
    Popularity
}

@Composable
fun MovieDetailsContent(
    movie: MovieDetailsDataModel,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    isFavoriteLoading: Boolean = false,
    isOfflineFavorite: Boolean = false,
    cast: List<CastMemberDataModel> = emptyList(),
    trailers: List<TrailerCarouselItem> = emptyList(),
    similarMovies: List<PosterCarouselItem> = emptyList(),
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCastClick: (Int) -> Unit,
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
                backdropUrl = (movie.backdropPath ?: movie.posterPath).toTmdbImageUrl(),
                posterUrl = movie.posterPath.toTmdbImageUrl(),
                metadata = listOfNotNull(
                    movie.releaseDate?.take(4),
                    movie.runtime?.let { "$it min" }
                ),
                rating = String.format("%.1f", movie.voteAverage),
                genres = movie.genres.map { it.name },
                isFavorite = isFavorite,
                isFavoriteLoading = isFavoriteLoading,
                onBackClick = onBackClick,
                onFavoriteClick = onFavoriteClick
            )
        }

        if (isOfflineFavorite) {
            item {
                OfflineFavoriteNotice(
                    modifier = Modifier.padding(top = ReelsDimensions.detailsOverviewTopSpacing)
                )
            }

            if (cast.isEmpty() && trailers.isEmpty() && similarMovies.isEmpty()) {
                item {
                    OfflineExtrasUnavailableNotice(
                        modifier = Modifier.padding(top = ReelsDimensions.detailsSectionSpacing)
                    )
                }
            }
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
                popularityLabel = "%,d votes".format(movie.voteCount),
                modifier = Modifier.padding(top = ReelsDimensions.detailsSectionSpacing)
            )
        }

        val hasMissingOnlineExtras = !isOfflineFavorite &&
            (cast.isEmpty() || trailers.isEmpty() || similarMovies.isEmpty())
        val areAllOnlineExtrasMissing = !isOfflineFavorite &&
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
                    onCastClick = onCastClick,
                    onSeeAllClick = onCastSeeAllClick
                )
            }
        }

        if (!areAllOnlineExtrasMissing && !isOfflineFavorite && trailers.isEmpty()) {
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

        if (!areAllOnlineExtrasMissing && !isOfflineFavorite && similarMovies.isEmpty()) {
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

@Composable
private fun MovieDetailsInlineNotice(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsSpacing.screenHorizontal),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            width = 1.dp,
            color = ReelsBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(ReelsDimensions.inlineStateCardPadding),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(ReelsDimensions.inlineStateIconContainerSize),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.inlineStateIconSize)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun OverviewSection(
    overview: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsSpacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.detailsSectionContentSpacing)
    ) {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = overview,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MovieInfoSection(
    releaseDate: String?,
    originalLanguage: String?,
    popularityLabel: String?,
    modifier: Modifier = Modifier,
) {
    val rows = listOfNotNull(
        releaseDate?.takeIf { it.isNotBlank() }?.let {
            MovieInfoRowData(
                label = "Release Date",
                value = it.toDisplayTmdbDate() ?: it,
                icon = MovieInfoIcon.ReleaseDate
            )
        },
        originalLanguage?.takeIf { it.isNotBlank() }?.let {
            MovieInfoRowData(
                label = "Original Language",
                value = it.toDisplayLanguageName(),
                icon = MovieInfoIcon.Language
            )
        },
        popularityLabel?.takeIf { it.isNotBlank() }?.let {
            MovieInfoRowData(
                label = "Popularity",
                value = it,
                icon = MovieInfoIcon.Popularity
            )
        }
    )

    if (rows.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsSpacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.detailsInfoRowSpacing)
    ) {
        rows.forEach { row ->
            MovieInfoRow(row = row)
        }
    }
}


@Composable
private fun MovieInfoRow(
    row: MovieInfoRowData,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary,
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
            )
        ) {
            Box(
                modifier = Modifier.size(ReelsDimensions.detailsInfoIconContainerSize),
                contentAlignment = Alignment.Center
            ) {
                MovieInfoIcon(icon = row.icon)
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)
        ) {
            Text(
                text = row.label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = row.value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun MovieInfoIcon(
    icon: MovieInfoIcon,
    modifier: Modifier = Modifier,
) {
    val imageVector = when (icon) {
        MovieInfoIcon.ReleaseDate -> Icons.Filled.CalendarToday
        MovieInfoIcon.Language -> Icons.Filled.Language
        MovieInfoIcon.Popularity -> Icons.AutoMirrored.Filled.TrendingUp
    }

    Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = modifier.size(ReelsDimensions.detailsInfoIconSize)
    )
}

private fun String.toDisplayLanguageName(): String {
    return Locale.forLanguageTag(this)
        .getDisplayLanguage(Locale.US)
        .replaceFirstChar { firstChar ->
            if (firstChar.isLowerCase()) {
                firstChar.titlecase(Locale.US)
            } else {
                firstChar.toString()
            }
        }
        .takeIf { language -> language.isNotBlank() }
        ?: this
}

@Composable
private fun OfflineExtrasUnavailableNotice(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsSpacing.screenHorizontal),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            width = 1.dp,
            color = ReelsBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(ReelsSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(ReelsDimensions.detailsOfflineIconContainerSize),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.WifiOff,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.detailsOfflineIconSize)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)) {
                Text(
                    text = "Extras unavailable offline",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Cast, trailers, and similar movies need a network refresh.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun OfflineFavoriteNotice(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsSpacing.screenHorizontal),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            width = 1.dp,
            color = ReelsBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(ReelsSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(ReelsDimensions.detailsOfflineIconContainerSize),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.DownloadDone,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.detailsOfflineIconSize)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)) {
                Text(
                    text = "Available offline",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Showing saved details. Cast, trailers, and similar movies will refresh when you reconnect.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private val previewCast = listOf(
    CastMemberDataModel(
        id = 1,
        name = "Timothée Chalamet",
        characterName = "Paul Atreides",
        profilePath = null
    ),
    CastMemberDataModel(
        id = 2,
        name = "Zendaya",
        characterName = "Chani",
        profilePath = null
    ),
    CastMemberDataModel(
        id = 3,
        name = "Rebecca Ferguson",
        characterName = "Lady Jessica",
        profilePath = null
    ),
    CastMemberDataModel(
        id = 4,
        name = "Josh Brolin",
        characterName = "Gurney Halleck",
        profilePath = null
    )
)

private val previewTrailers = listOf(
    TrailerCarouselItem(
        id = "official",
        title = "Official Trailer",
        thumbnailUrl = null,
        source = "YouTube",
        duration = "3:12"
    ),
    TrailerCarouselItem(
        id = "final",
        title = "Final Trailer",
        thumbnailUrl = null,
        source = "YouTube",
        duration = "2:45"
    )
)

private val previewSimilarMovies = listOf(
    PosterCarouselItem(
        id = 1,
        title = "Oppenheimer",
        posterUrl = null
    ),
    PosterCarouselItem(
        id = 2,
        title = "Poor Things",
        posterUrl = null
    ),
    PosterCarouselItem(
        id = 3,
        title = "Killers of the Flower Moon",
        posterUrl = null
    )
)

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
            cast = previewCast,
            trailers = previewTrailers,
            similarMovies = previewSimilarMovies,
            onBackClick = {},
            onFavoriteClick = {},
            onCastClick = {},
            onCastSeeAllClick = {},
            onTrailerClick = {},
            onSimilarMovieClick = {}
        )
    }
}
