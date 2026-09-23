/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.cast

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CastMemberDataModel(
    val id: Int,
    val name: String,
    val characterName: String,
    val profilePath: String?
) : Parcelable