package com.norbertotaveras.reels.model.data.person

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonMovieCreditsDataModel(
    val cast: List<PersonMovieCreditDataModel>
) : Parcelable
