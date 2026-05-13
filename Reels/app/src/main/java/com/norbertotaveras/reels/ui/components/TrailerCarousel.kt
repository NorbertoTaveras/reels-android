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

data class TrailerCarouselItem(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val source: String? = null,
    val duration: String? = null,
)

@Composable
fun TrailerCarousel(
    trailers: List<TrailerCarouselItem>,
    onTrailerClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (trailers.isEmpty()) return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)
    ) {
        SectionHeader(
            modifier = Modifier.padding(horizontal = ReelsSpacing.screenHorizontal),
            title = "Trailers"
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = ReelsSpacing.screenHorizontal),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
        ) {
            items(
                items = trailers,
                key = { trailer -> trailer.id }
            ) { trailer ->
                TrailerCard(
                    title = trailer.title,
                    thumbnailUrl = trailer.thumbnailUrl,
                    source = trailer.source,
                    duration = trailer.duration,
                    onClick = { onTrailerClick(trailer.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TrailerCarouselPreview() {
    ReelsTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = ReelsSpacing.large)
        ) {
            TrailerCarousel(
                trailers = listOf(
                    TrailerCarouselItem(
                        id = "official-trailer",
                        title = "Official Trailer",
                        thumbnailUrl = null,
                        source = "YouTube",
                        duration = "3:12"
                    ),
                    TrailerCarouselItem(
                        id = "final-trailer",
                        title = "Final Trailer",
                        thumbnailUrl = null,
                        source = "YouTube",
                        duration = "2:45"
                    ),
                    TrailerCarouselItem(
                        id = "behind-scenes",
                        title = "Behind the Scenes",
                        thumbnailUrl = null,
                        source = "YouTube",
                        duration = "5:20"
                    )
                ),
                onTrailerClick = {}
            )
        }
    }
}
