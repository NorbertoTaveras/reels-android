/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.movie.dto

import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.domain.movie.model.GenreDataModel

data class GenresResponseDto(
    @SerializedName("genres")
    val genres: List<GenreDto>?
)

internal fun GenresResponseDto.toGenreDataModels(): List<GenreDataModel> {
    return genres.orEmpty().map { genre ->
        genre.toGenreDataModel()
    }
}
