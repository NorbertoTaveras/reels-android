/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.genre

import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.domain.model.genre.GenreDataModel

data class GenreDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

internal fun GenreDto.toGenreDataModel(): GenreDataModel {
    return GenreDataModel(
        id = id,
        name = name
    )
}
