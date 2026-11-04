/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.cast

import com.norbertotaveras.reels.domain.person.model.CastMemberDataModel

data class MovieCastUiState(
    val isLoading: Boolean = true,
    val cast: List<CastMemberDataModel> = emptyList(),
    val savedPersonIds: Set<Int> = emptySet(),
    val savingPersonIds: Set<Int> = emptySet(),
    val errorMessage: String? = null,
)
