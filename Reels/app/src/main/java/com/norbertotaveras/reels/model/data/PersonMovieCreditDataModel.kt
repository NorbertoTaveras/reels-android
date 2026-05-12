package com.norbertotaveras.reels.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonMovieCreditDataModel(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val character: String?,
    val voteAverage: Double,
    val popularity: Double
) : Parcelable
