package com.norbertotaveras.reels.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieVideosDataModel(
    val videos: List<MovieVideoDataModel>
) : Parcelable
