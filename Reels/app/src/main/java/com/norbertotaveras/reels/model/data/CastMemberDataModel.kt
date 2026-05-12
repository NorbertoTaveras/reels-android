package com.norbertotaveras.reels.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CastMemberDataModel(
    val id: Int,
    val name: String,
    val characterName: String,
    val profilePath: String?
) : Parcelable
