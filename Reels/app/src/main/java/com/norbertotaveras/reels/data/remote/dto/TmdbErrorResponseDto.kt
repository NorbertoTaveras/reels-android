/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TmdbErrorResponseDto(
    @SerializedName("status_code")
    val statusCode: Int?,
)
