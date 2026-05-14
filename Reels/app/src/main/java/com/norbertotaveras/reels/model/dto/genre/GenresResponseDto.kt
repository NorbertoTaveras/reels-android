package com.norbertotaveras.reels.model.dto.genre

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.model.data.genre.GenreDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenresResponseDto(
    @SerializedName("genres")
    val genres: List<GenreDto>?
) : Parcelable {
    fun toGenreDataModels(): List<GenreDataModel> {
        return genres.orEmpty().map { genre ->
            genre.toGenreDataModel()
        }
    }
}
