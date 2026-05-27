package com.norbertotaveras.reels.data.remote.dto.movie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
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
	@SerializedName("backdrop_path")
	val backdropPath: String?,
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
			backdropPath = backdropPath,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
			genreIds = genreIds
		)
	}
}
