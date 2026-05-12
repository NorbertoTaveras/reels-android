package com.norbertotaveras.reels.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieCreditsDataModel(
    val id: Int,
    val cast: List<CastMemberDataModel>
) : Parcelable
