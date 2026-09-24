/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.account

import com.google.gson.annotations.SerializedName
import com.norbertotaveras.reels.domain.model.account.TmdbAccountDataModel

data class TmdbAccountDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("name")
    val name: String?,
) {
    fun toTmdbAccountDataModel(): TmdbAccountDataModel {
        return TmdbAccountDataModel(
            accountId = id,
            username = username,
            displayName = name
        )
    }
}
