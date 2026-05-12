package com.norbertotaveras.reels.model.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.model.data.GenreDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenreDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
) : Parcelable{
    fun toGenreDataModel(): GenreDataModel {
        return GenreDataModel(
            id = id,
            name = name
        )
    }
}
