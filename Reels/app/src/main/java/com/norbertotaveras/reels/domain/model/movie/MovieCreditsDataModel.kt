/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.movie

import android.os.Parcelable
import com.norbertotaveras.reels.domain.model.cast.CastMemberDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieCreditsDataModel(
    val id: Int,
    val cast: List<CastMemberDataModel>
) : Parcelable
