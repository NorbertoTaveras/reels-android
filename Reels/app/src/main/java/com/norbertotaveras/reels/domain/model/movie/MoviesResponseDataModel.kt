/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.movie

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MoviesResponseDataModel(
    val page: Int,
    val results: List<MovieDataModel>,
    val totalPages: Int,
    val totalResults: Int
) : Parcelable
