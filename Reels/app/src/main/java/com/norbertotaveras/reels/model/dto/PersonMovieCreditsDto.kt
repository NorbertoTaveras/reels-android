package com.norbertotaveras.reels.model.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.model.data.PersonMovieCreditsDataModel
import kotlinx.parcelize.Parcelize
import kotlin.collections.orEmpty

@Parcelize
data class PersonMovieCreditsDto(
    @SerializedName("cast")
    val cast: List<PersonMovieCreditDto>?
) : Parcelable {
    fun toPersonMovieCreditsDataModel(): PersonMovieCreditsDataModel {
        return PersonMovieCreditsDataModel(
            cast = cast
                .orEmpty()
                .filter { it.id != null && !it.title.isNullOrBlank() }
                .map { it.toPersonMovieCreditDataModel() }
                .sortedByDescending { it.popularity }
        )
    }
}