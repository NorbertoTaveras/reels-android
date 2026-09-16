package com.norbertotaveras.reels.presentation.features.movies.cast

import com.norbertotaveras.reels.domain.model.cast.CastMemberDataModel

data class MovieCastUiState(
    val isLoading: Boolean = true,
    val cast: List<CastMemberDataModel> = emptyList(),
    val errorMessage: String? = null,
)
