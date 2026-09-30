/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.mapper.genre

import com.norbertotaveras.reels.data.remote.dto.genre.GenreDto
import com.norbertotaveras.reels.data.remote.dto.genre.GenresResponseDto
import com.norbertotaveras.reels.domain.model.genre.GenreDataModel

fun GenreDto.toGenreDataModel(): GenreDataModel {
    return GenreDataModel(
        id = id,
        name = name
    )
}

fun GenresResponseDto.toGenreDataModels(): List<GenreDataModel> {
    return genres.orEmpty().map { genre ->
        genre.toGenreDataModel()
    }
}
