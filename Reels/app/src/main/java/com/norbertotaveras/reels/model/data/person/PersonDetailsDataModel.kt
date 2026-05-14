package com.norbertotaveras.reels.model.data.person

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonDetailsDataModel(
    val id: Int,
    val name: String,
    val biography: String,
    val birthday: String?,
    val placeOfBirth: String?,
    val profilePath: String?,
    val knownForDepartment: String?
) : Parcelable
