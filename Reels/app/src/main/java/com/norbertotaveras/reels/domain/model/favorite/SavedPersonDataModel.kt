/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.favorite

import android.os.Parcelable
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class SavedPersonDataModel(
    val id: Int,
    val name: String,
    val biography: String,
    val birthday: String?,
    val placeOfBirth: String?,
    val profilePath: String?,
    val knownForDepartment: String?,
    val knownFor: List<PersonMovieCreditDataModel>,
    val savedAt: Long
) : Parcelable
