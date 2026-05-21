package com.norbertotaveras.reels.domain.model.movie

import android.os.Parcelable
import com.norbertotaveras.reels.domain.model.cast.CastMemberDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieCreditsDataModel(
    val id: Int,
    val cast: List<CastMemberDataModel>
) : Parcelable
