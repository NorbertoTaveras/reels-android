/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.account.mapper

import com.google.gson.JsonElement
import com.norbertotaveras.reels.data.remote.account.dto.TmdbAccountDto
import com.norbertotaveras.reels.data.remote.account.dto.TmdbMovieAccountStateDto
import com.norbertotaveras.reels.domain.account.model.TmdbAccountDataModel
import com.norbertotaveras.reels.domain.account.model.TmdbMovieAccountStateDataModel

fun TmdbAccountDto.toTmdbAccountDataModel(): TmdbAccountDataModel {
    return TmdbAccountDataModel(
        accountId = id,
        username = username,
        displayName = name,
        avatarPath = avatar?.tmdb?.avatarPath,
        gravatarHash = avatar?.gravatar?.hash
    )
}

fun TmdbMovieAccountStateDto.toTmdbMovieAccountStateDataModel(): TmdbMovieAccountStateDataModel {
    return TmdbMovieAccountStateDataModel(
        movieId = id,
        isWatchlisted = watchlist,
        rating = rated.toRatingValue()
    )
}

private fun JsonElement?.toRatingValue(): Double? {
    return this
        ?.takeIf { rated -> rated.isJsonObject }
        ?.asJsonObject
        ?.get("value")
        ?.takeIf { value -> value.isJsonPrimitive }
        ?.asDouble
}
