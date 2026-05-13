package com.norbertotaveras.reels.features.movies.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.model.data.CastMemberDataModel
import com.norbertotaveras.reels.model.data.GenreDataModel
import com.norbertotaveras.reels.model.data.MovieDetailsDataModel
import com.norbertotaveras.reels.ui.components.CastCarousel
import com.norbertotaveras.reels.ui.components.MovieHeroHeader
import com.norbertotaveras.reels.ui.components.PosterCarouselItem
import com.norbertotaveras.reels.ui.components.SimilarMoviesSection
import com.norbertotaveras.reels.ui.components.TrailerCarousel
import com.norbertotaveras.reels.ui.components.TrailerCarouselItem
import com.norbertotaveras.reels.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.ui.theme.ReelsTheme

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
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xLarge)
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

        item {
            OverviewSection(
                overview = movie.overview.ifBlank { "No overview available yet." }
            )
        }

        item {
            MovieInfoSection(
                releaseDate = movie.releaseDate,
                originalLanguage = movie.originalLanguage,
                popularityLabel = "%,d votes".format(movie.voteCount)
            )
        }

        if (cast.isNotEmpty()) {
            item {
                CastCarousel(
                    cast = cast,
                    onCastClick = onCastClick,
                    onSeeAllClick = onCastSeeAllClick
                )
            }
        }

        if (trailers.isNotEmpty()) {
            item {
                TrailerCarousel(
                    trailers = trailers,
                    onTrailerClick = onTrailerClick
                )
            }
        }

        if (similarMovies.isNotEmpty()) {
            item {
                SimilarMoviesSection(
                    movies = similarMovies,
                    onMovieClick = onSimilarMovieClick
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
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
    ) {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = overview,
            style = MaterialTheme.typography.bodyLarge,
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
                value = it,
                icon = MovieInfoIcon.ReleaseDate
            )
        },
        originalLanguage?.takeIf { it.isNotBlank() }?.let {
            MovieInfoRowData(
                label = "Original Language",
                value = it,
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
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
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
    androidx.compose.foundation.layout.Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            shape = MaterialTheme.shapes.medium,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
            )
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                MovieInfoIcon(icon = row.icon)
            }
        }

        Column {
            Text(
                text = row.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = row.value,
                style = MaterialTheme.typography.bodyMedium,
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
        MovieInfoIcon.Popularity -> Icons.Filled.TrendingUp
    }

    Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = modifier.size(16.dp)
    )
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

