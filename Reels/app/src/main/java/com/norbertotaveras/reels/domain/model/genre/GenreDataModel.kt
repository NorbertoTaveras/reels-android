package com.norbertotaveras.reels.domain.model.genre

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenreDataModel(
    val id: Int,
    val name: String
) : Parcelable