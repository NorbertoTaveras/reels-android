package com.norbertotaveras.reels.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.norbertotaveras.reels.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.ui.theme.ReelsTheme

data class PosterCarouselItem(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val releaseYear: String? = null,
    val rating: String? = null,
)

@Composable
fun PosterCarousel(
    items: List<PosterCarouselItem>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = ReelsSpacing.large),
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
    ) {
        items(
            items = items,
            key = { item -> item.id }
        ) { item ->
            MoviePosterCard(
                title = item.title,
                posterUrl = item.posterUrl,
                releaseYear = item.releaseYear,
                rating = item.rating,
                onClick = { onMovieClick(item.id) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PosterCarouselPreview() {
    ReelsTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = ReelsSpacing.large)
        ) {
            PosterCarousel(
                items = listOf(
                    PosterCarouselItem(
                        id = 1,
                        title = "Dune: Part Two",
                        posterUrl = null,
                        releaseYear = "2024",
                        rating = "8.4"
                    ),
                    PosterCarouselItem(
                        id = 2,
                        title = "Oppenheimer",
                        posterUrl = null,
                        releaseYear = "2023",
                        rating = "8.1"
                    ),
                    PosterCarouselItem(
                        id = 3,
                        title = "Poor Things",
                        posterUrl = null,
                        releaseYear = "2023",
                        rating = "7.8"
                    )
                ),
                onMovieClick = {}
            )
        }
    }
}
