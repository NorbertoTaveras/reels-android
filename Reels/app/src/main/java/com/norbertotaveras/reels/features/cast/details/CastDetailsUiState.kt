package com.norbertotaveras.reels.features.cast.details

import com.norbertotaveras.reels.model.data.person.PersonDetailsDataModel
import com.norbertotaveras.reels.model.data.person.PersonMovieCreditDataModel

data class CastDetailsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val person: PersonDetailsDataModel? = null,
    val knownFor: List<PersonMovieCreditDataModel> = emptyList(),
)
