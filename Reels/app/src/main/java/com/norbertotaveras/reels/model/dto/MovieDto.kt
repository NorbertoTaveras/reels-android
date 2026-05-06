package com.norbertotaveras.reels.model.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.model.data.MovieDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieDto(
	@SerializedName("id")
	val id: Int,
	@SerializedName("title")
	val title: String,
	@SerializedName("overview")
	val overview: String,
	@SerializedName("poster_path")
	val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("vote_average")
    val voteAverage: Double
) : Parcelable {
	fun toMovieDataModel(): MovieDataModel {
		return MovieDataModel(
			id = id,
			title = title,
			overview = overview,
			posterPath = posterPath,
            releaseDate = releaseDate,
            voteAverage = voteAverage
		)
	}
}
