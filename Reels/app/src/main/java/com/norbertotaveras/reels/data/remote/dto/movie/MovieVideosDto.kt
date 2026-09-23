/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.movie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.domain.model.movie.MovieVideoDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieVideosDataModel
import kotlinx.parcelize.Parcelize
import kotlin.collections.orEmpty

@Parcelize
data class MovieVideosDto(
    @SerializedName("results")
    val results: List<MovieVideoDto>?
) : Parcelable {
    fun toMovieVideosDataModel(): MovieVideosDataModel {
        return MovieVideosDataModel(
            videos = results
                .orEmpty()
                .filter { it.id != null && !it.key.isNullOrBlank() }
                .map { it.toMovieVideoDataModel() }
                .sortedWith(
                    compareByDescending<MovieVideoDataModel> { it.official }
                        .thenBy { video ->
                            when (video.type.lowercase()) {
                                "trailer" -> 0
                                "teaser" -> 1
                                "clip" -> 2
                                "featurette" -> 3
                                else -> 4
                            }
                        }
                )
        )
    }
}
