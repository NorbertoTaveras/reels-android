package com.norbertotaveras.reels.model.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.model.data.PersonMovieCreditDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonMovieCreditDto(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("character")
    val character: String?,

    @SerializedName("vote_average")
    val voteAverage: Double?,

    @SerializedName("popularity")
    val popularity: Double?
) : Parcelable {
    fun toPersonMovieCreditDataModel(): PersonMovieCreditDataModel {
        return PersonMovieCreditDataModel(
            id = requireNotNull(id),
            title = title.orEmpty(),
            posterPath = posterPath,
            releaseDate = releaseDate,
            character = character,
            voteAverage = voteAverage ?: 0.0,
            popularity = popularity ?: 0.0
        )
    }
}