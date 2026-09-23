/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.genre

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.domain.model.genre.GenreDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenresResponseDto(
    @SerializedName("genres")
    val genres: List<GenreDto>?
) : Parcelable {
    fun toGenreDataModels(): List<GenreDataModel> {
        return genres.orEmpty().map { genre ->
            genre.toGenreDataModel()
        }
    }
}
