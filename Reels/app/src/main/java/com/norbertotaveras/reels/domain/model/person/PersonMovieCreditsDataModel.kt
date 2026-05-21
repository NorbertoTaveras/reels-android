package com.norbertotaveras.reels.domain.model.person

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonMovieCreditsDataModel(
    val cast: List<PersonMovieCreditDataModel>
) : Parcelable
