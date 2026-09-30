/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.mapper.cast

import com.norbertotaveras.reels.data.remote.dto.cast.CastMemberDto
import com.norbertotaveras.reels.domain.model.cast.CastMemberDataModel

fun CastMemberDto.toCastMemberDataModel(): CastMemberDataModel {
    return CastMemberDataModel(
        id = id,
        name = name,
        characterName = character.orEmpty(),
        profilePath = profilePath
    )
}
