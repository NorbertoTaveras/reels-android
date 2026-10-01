/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.mapper.favorite

import com.norbertotaveras.reels.domain.model.favorite.SavedPersonDataModel
import com.norbertotaveras.reels.domain.model.person.PersonDetailsDataModel

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
