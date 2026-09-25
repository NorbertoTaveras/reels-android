/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.account

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.domain.model.account.TmdbMovieAccountStateDataModel

data class TmdbMovieAccountStateDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("favorite")
    val favorite: Boolean,
    @SerializedName("watchlist")
    val watchlist: Boolean,
    @SerializedName("rated")
    val rated: JsonElement?,
) {
    fun toTmdbMovieAccountStateDataModel(): TmdbMovieAccountStateDataModel {
        return TmdbMovieAccountStateDataModel(
            movieId = id,
            isWatchlisted = watchlist,
            rating = rated.toRatingValue()
        )
    }
}

private fun JsonElement?.toRatingValue(): Double? {
    return this
        ?.takeIf { rated -> rated.isJsonObject }
        ?.asJsonObject
        ?.get("value")
        ?.takeIf { value -> value.isJsonPrimitive }
        ?.asDouble
}
