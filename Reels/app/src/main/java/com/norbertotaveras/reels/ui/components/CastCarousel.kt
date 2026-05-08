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

data class CastCarouselItem(
    val id: Int,
    val name: String,
    val characterName: String,
    val profileUrl: String?,
)

@Composable
fun CastCarousel(
    cast: List<CastCarouselItem>,
    onCastClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null,
) {
    if (cast.isEmpty()) return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)
    ) {
        SectionHeader(
            title = "Cast",
            actionLabel = if (onSeeAllClick != null) "See all" else null,
            onActionClick = onSeeAllClick
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = ReelsSpacing.screenHorizontal),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
        ) {
            items(
                items = cast,
                key = { item -> item.id }
            ) { item ->
                CastCard(
                    name = item.name,
                    characterName = item.characterName,
                    profileUrl = item.profileUrl,
                    onClick = { onCastClick(item.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CastCarouselPreview() {
    ReelsTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = ReelsSpacing.large)
        ) {
            CastCarousel(
                cast = listOf(
                    CastCarouselItem(
                        id = 1,
                        name = "Timothée Chalamet",
                        characterName = "Paul Atreides",
                        profileUrl = null
                    ),
                    CastCarouselItem(
                        id = 2,
                        name = "Zendaya",
                        characterName = "Chani",
                        profileUrl = null
                    ),
                    CastCarouselItem(
                        id = 3,
                        name = "Rebecca Ferguson",
                        characterName = "Lady Jessica",
                        profileUrl = null
                    ),
                    CastCarouselItem(
                        id = 4,
                        name = "Josh Brolin",
                        characterName = "Gurney Halleck",
                        profileUrl = null
                    )
                ),
                onCastClick = {},
                onSeeAllClick = {}
            )
        }
    }
}
