/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.person

import com.google.gson.annotations.SerializedName

data class PersonMovieCreditsDto(
    @SerializedName("cast")
    val cast: List<PersonMovieCreditDto>?
)
