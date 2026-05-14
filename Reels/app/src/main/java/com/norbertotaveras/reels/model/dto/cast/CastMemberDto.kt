package com.norbertotaveras.reels.model.dto.cast

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.model.data.cast.CastMemberDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CastMemberDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("character")
    val character: String?,

    @SerializedName("profile_path")
    val profilePath: String?
) : Parcelable {
    fun toCastMemberDataModel(): CastMemberDataModel {
        return CastMemberDataModel(
            id = id,
            name = name,
            characterName = character.orEmpty(),
            profilePath = profilePath
        )
    }
}