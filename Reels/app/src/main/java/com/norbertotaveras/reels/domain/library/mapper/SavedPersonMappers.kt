/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.library.mapper

import com.norbertotaveras.reels.domain.library.model.SavedPersonDataModel
import com.norbertotaveras.reels.domain.person.model.PersonDetailsDataModel

fun SavedPersonDataModel.toPersonDetailsDataModel(): PersonDetailsDataModel {
    return PersonDetailsDataModel(
        id = id,
        name = name,
        biography = biography,
        birthday = birthday,
        placeOfBirth = placeOfBirth,
        profilePath = profilePath,
        knownForDepartment = knownForDepartment
    )
}
