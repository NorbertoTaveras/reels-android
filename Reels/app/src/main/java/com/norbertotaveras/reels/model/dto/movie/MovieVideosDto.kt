package com.norbertotaveras.reels.model.dto.movie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.model.data.movie.MovieVideoDataModel
import com.norbertotaveras.reels.model.data.movie.MovieVideosDataModel
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
