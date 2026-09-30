/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.movie

import com.google.gson.annotations.SerializedName

data class MovieVideoDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("key")
    val key: String?,

    @SerializedName("site")
    val site: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("official")
    val official: Boolean?
)
