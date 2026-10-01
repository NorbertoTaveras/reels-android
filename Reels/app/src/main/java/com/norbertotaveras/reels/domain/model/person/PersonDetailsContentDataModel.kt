/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.person

data class PersonDetailsContentDataModel(
    val person: PersonDetailsDataModel,
    val knownFor: List<PersonMovieCreditDataModel>,
    val isOfflineSavedPerson: Boolean,
)
