package com.norbertotaveras.reels.model.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.model.data.MovieDetailsDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
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
    @SerializedName("genres")
    val genres: List<GenreDto>,
) : Parcelable{
    fun toMovieDetailsDataModel(): MovieDetailsDataModel {
        return MovieDetailsDataModel(
            id = id,
            title = title,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseDate = releaseDate,
            runtime = runtime,
            voteAverage = voteAverage,
            genres = genres.map { it.toGenreDataModel() }
        )
    }
}
