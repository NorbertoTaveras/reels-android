package com.norbertotaveras.reels.model.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.model.data.MovieVideoDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieVideoDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("key")
    val key: String?,

    @SerializedName("site")
    val site: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("official")
    val official: Boolean?
) : Parcelable {
    fun toMovieVideoDataModel(): MovieVideoDataModel {
        return MovieVideoDataModel(
            id = requireNotNull(id),
            name = name.orEmpty(),
            key = key.orEmpty(),
            site = site.orEmpty(),
            type = type.orEmpty(),
            official = official ?: false
        )
    }
}