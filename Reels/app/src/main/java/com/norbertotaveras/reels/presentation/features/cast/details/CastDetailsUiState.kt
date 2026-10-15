/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.details

import com.norbertotaveras.reels.domain.model.person.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditDataModel

data class CastDetailsUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isOnline: Boolean = true,
    val isOfflineSavedPerson: Boolean = false,
    val errorMessage: String? = null,
    val person: PersonDetailsDataModel? = null,
    val knownFor: List<PersonMovieCreditDataModel> = emptyList(),
)
