package com.norbertotaveras.reels.presentation.features.cast.details

import com.norbertotaveras.reels.domain.model.person.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditDataModel

data class CastDetailsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val person: PersonDetailsDataModel? = null,
    val knownFor: List<PersonMovieCreditDataModel> = emptyList(),
)
