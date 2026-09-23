/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.movie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.domain.model.movie.MoviesResponseDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MoviesResponseDto(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<MovieDto>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
) : Parcelable {
    fun toMoviesResponseDataModel(): MoviesResponseDataModel {
        return MoviesResponseDataModel(
            page = page,
            results = results.map { it.toMovieDataModel() },
            totalPages = totalPages,
            totalResults = totalResults
        )
    }
}

