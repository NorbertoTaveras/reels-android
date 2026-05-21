package com.norbertotaveras.reels.domain.model.movie

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieVideosDataModel(
    val videos: List<MovieVideoDataModel>
) : Parcelable
