package com.norbertotaveras.reels.model.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.model.data.MovieCreditsDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieCreditsDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("cast")
    val cast: List<CastMemberDto>
) : Parcelable {
    fun toMovieCreditsDataModel(): MovieCreditsDataModel {
        return MovieCreditsDataModel(
            id = id,
            cast = cast.map { it.toCastMemberDataModel() }
        )
    }
}
