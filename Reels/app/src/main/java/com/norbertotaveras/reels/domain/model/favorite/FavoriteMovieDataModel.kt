/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.favorite

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FavoriteMovieDataModel(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val runtime: Int?,
    val voteAverage: Double,
    val voteCount: Int,
    val originalLanguage: String?,
    val popularity: Double,
    val genreNames: List<String>,
    val favoritedAt: Long
) : Parcelable
