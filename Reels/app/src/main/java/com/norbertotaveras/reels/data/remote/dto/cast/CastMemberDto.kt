/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.cast

import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.domain.model.cast.CastMemberDataModel

data class CastMemberDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("character")
    val character: String?,

    @SerializedName("profile_path")
    val profilePath: String?
)

internal fun CastMemberDto.toCastMemberDataModel(): CastMemberDataModel {
    return CastMemberDataModel(
        id = id,
        name = name,
        characterName = character.orEmpty(),
        profilePath = profilePath
    )
}
