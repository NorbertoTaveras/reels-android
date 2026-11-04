/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.person.details

import com.norbertotaveras.reels.domain.person.model.PersonDetailsContentDataModel

internal fun CastDetailsUiState.withContent(
    content: PersonDetailsContentDataModel,
): CastDetailsUiState {
    return copy(
        person = content.person,
        knownFor = content.knownFor,
        isOfflineSavedPerson = isSaved && !isOnline,
        errorMessage = null,
        isLoading = false
    )
}
