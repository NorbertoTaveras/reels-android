package com.norbertotaveras.reels.ui.components.cast

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
import com.norbertotaveras.reels.model.data.cast.CastMemberDataModel
import com.norbertotaveras.reels.ui.components.common.SectionHeader
import com.norbertotaveras.reels.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.ui.theme.ReelsTheme

@Composable
fun CastCarousel(
    cast: List<CastMemberDataModel>,
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
            modifier = Modifier.padding(horizontal = ReelsSpacing.screenHorizontal),
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
                key = { castMember -> castMember.id }
            ) { castMember ->
                CastCard(
                    name = castMember.name,
                    characterName = castMember.characterName,
                    profileUrl = castMember.profilePath.toTmdbImageUrl(),
                    onClick = { onCastClick(castMember.id) }
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
                ),
                onCastClick = {},
                onSeeAllClick = {}
            )
        }
    }
}
