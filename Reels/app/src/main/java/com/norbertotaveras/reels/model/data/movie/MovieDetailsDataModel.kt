package com.norbertotaveras.reels.model.data.movie

import android.os.Parcelable
import com.norbertotaveras.reels.model.data.genre.GenreDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieDetailsDataModel(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val runtime: Int?,
    val voteAverage: Double,
    val originalLanguage: String?,
    val popularity: Double,
    val voteCount: Int,
    val genres: List<GenreDataModel>
) : Parcelable
