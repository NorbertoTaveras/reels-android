/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.mapper

import com.norbertotaveras.reels.data.local.entity.TmdbMovieActivityEntity
import com.norbertotaveras.reels.domain.model.account.TmdbLibraryList
import com.norbertotaveras.reels.domain.model.account.TmdbMovieActivityDataModel

fun TmdbMovieActivityEntity.toTmdbMovieActivityDataModel(): TmdbMovieActivityDataModel? {
    val libraryList = TmdbLibraryList.entries.firstOrNull { list -> list.name == listType }
        ?: return null
    return TmdbMovieActivityDataModel(
        movieId = movieId,
        listType = libraryList,
        occurredAtMillis = occurredAtMillis
    )
}
