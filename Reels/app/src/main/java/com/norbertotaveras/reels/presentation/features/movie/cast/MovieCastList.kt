/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.cast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.norbertotaveras.reels.domain.person.model.CastMemberDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.EmptyState
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun MovieCastContent(
    cast: List<CastMemberDataModel>,
    savedPersonIds: Set<Int>,
    savingPersonIds: Set<Int>,
    onBackClick: () -> Unit,
    onCastClick: (Int) -> Unit,
    onSaveClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = ReelsSpacing.screenHorizontal,
            top = ReelsDimensions.fullCastScreenTopPadding,
            end = ReelsSpacing.screenHorizontal,
            bottom = ReelsDimensions.fullCastScreenBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.fullCastRowSpacing)
    ) {
        item {
            MovieCastHeader(
                modifier = Modifier.padding(bottom = ReelsDimensions.fullCastHeaderBottomPadding),
                onBackClick = onBackClick
            )
        }

        if (cast.isEmpty()) {
            item {
                EmptyState(
                    title = "No cast found",
                    message = "This movie does not have cast information available yet."
                )
            }
        } else {
            items(
                items = cast,
                key = { castMember -> castMember.id }
            ) { castMember ->
                MovieCastRow(
                    castMember = castMember,
                    isSaved = castMember.id in savedPersonIds,
                    isSaving = castMember.id in savingPersonIds,
                    onClick = {
                        onCastClick(castMember.id)
                    },
                    onSaveClick = {
                        onSaveClick(castMember.id)
                    }
                )
            }
        }
    }
}
