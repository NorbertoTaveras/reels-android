package com.norbertotaveras.reels.domain.model.movie

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MoviesResponseDataModel(
    val page: Int,
    val results: List<MovieDataModel>,
    val totalPages: Int,
    val totalResults: Int
) : Parcelable
