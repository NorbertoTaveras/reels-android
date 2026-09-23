/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.person

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditsDataModel
import kotlinx.parcelize.Parcelize
import kotlin.collections.orEmpty

@Parcelize
data class PersonMovieCreditsDto(
    @SerializedName("cast")
    val cast: List<PersonMovieCreditDto>?
) : Parcelable {
    fun toPersonMovieCreditsDataModel(): PersonMovieCreditsDataModel {
        return PersonMovieCreditsDataModel(
            cast = cast
                .orEmpty()
                .filter { it.id != null && !it.title.isNullOrBlank() }
                .map { it.toPersonMovieCreditDataModel() }
                .sortedByDescending { it.popularity }
        )
    }
}