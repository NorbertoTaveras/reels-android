/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.movie

import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.data.remote.dto.cast.CastMemberDto

data class MovieCreditsDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("cast")
    val cast: List<CastMemberDto>
)
