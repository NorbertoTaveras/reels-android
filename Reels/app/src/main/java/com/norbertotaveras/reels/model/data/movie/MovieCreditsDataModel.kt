package com.norbertotaveras.reels.model.data.movie

import android.os.Parcelable
import com.norbertotaveras.reels.model.data.cast.CastMemberDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieCreditsDataModel(
    val id: Int,
    val cast: List<CastMemberDataModel>
) : Parcelable
