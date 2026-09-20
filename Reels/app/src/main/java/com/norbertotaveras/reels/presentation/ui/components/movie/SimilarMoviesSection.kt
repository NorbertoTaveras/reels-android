package com.norbertotaveras.reels.presentation.ui.components.movie

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.norbertotaveras.reels.presentation.ui.components.common.SectionHeader
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme

@Composable
fun SimilarMoviesSection(
    movies: List<PosterCarouselItem>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (movies.isEmpty())
        return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.detailsCarouselHeaderSpacing)
    ) {
        SectionHeader(
            modifier = Modifier.padding(horizontal = ReelsSpacing.screenHorizontal),
            title = "Similar Movies",
            titleStyle = MaterialTheme.typography.titleMedium
        )

        PosterCarousel(
            items = movies,
            onMovieClick = onMovieClick,
            contentPadding = PaddingValues(
                horizontal = ReelsSpacing.screenHorizontal
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SimilarMoviesSectionPreview() {
    ReelsTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = ReelsSpacing.large)
        ) {
            SimilarMoviesSection(
                movies = listOf(
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
                ),
                onMovieClick = {}
            )
        }
    }
}
