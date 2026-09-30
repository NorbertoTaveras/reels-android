/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.movie

import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.data.remote.dto.genre.GenreDto

data class MovieDetailsDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("runtime")
    val runtime: Int?,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("original_language")
    val originalLanguage: String?,
    @SerializedName("popularity")
    val popularity: Double?,
    @SerializedName("vote_count")
    val voteCount: Int?,
    @SerializedName("genres")
    val genres: List<GenreDto>,
)
