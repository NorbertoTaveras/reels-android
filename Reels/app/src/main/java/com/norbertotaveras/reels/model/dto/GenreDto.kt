package com.norbertotaveras.reels.model.dto

import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.model.data.GenreDataModel

data class GenreDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
) {
    fun toGenreDataModel(): GenreDataModel {
        return GenreDataModel(
            id = id,
            name = name
        )
    }
}
