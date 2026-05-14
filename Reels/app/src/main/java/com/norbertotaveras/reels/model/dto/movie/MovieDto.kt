package com.norbertotaveras.reels.model.dto.movie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.model.data.movie.MovieDataModel
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
    val voteAverage: Double,
	@SerializedName("genre_ids")
	val genreIds: List<Int>
) : Parcelable {
	fun toMovieDataModel(): MovieDataModel {
		return MovieDataModel(
			id = id,
			title = title,
			overview = overview,
			posterPath = posterPath,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
			genreIds = genreIds
		)
	}
}
