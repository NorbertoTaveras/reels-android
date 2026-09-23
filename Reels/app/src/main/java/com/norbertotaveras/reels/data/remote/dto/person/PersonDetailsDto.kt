/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.person

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.domain.model.person.PersonDetailsDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonDetailsDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("biography")
    val biography: String?,

    @SerializedName("birthday")
    val birthday: String?,

    @SerializedName("place_of_birth")
    val placeOfBirth: String?,

    @SerializedName("profile_path")
    val profilePath: String?,

    @SerializedName("known_for_department")
    val knownForDepartment: String?
) : Parcelable{
    fun toPersonDetailsDataModel(): PersonDetailsDataModel {
        return PersonDetailsDataModel(
            id = id,
            name = name,
            biography = biography.orEmpty(),
            birthday = birthday,
            placeOfBirth = placeOfBirth,
            profilePath = profilePath,
            knownForDepartment = knownForDepartment
        )
    }
}
