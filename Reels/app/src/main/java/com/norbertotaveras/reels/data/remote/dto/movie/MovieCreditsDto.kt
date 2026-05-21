package com.norbertotaveras.reels.data.remote.dto.movie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.domain.model.movie.MovieCreditsDataModel
import com.norbertotaveras.reels.data.remote.dto.cast.CastMemberDto
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
