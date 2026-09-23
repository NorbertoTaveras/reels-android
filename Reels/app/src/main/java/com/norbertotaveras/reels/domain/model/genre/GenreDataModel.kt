/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.genre

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenreDataModel(
    val id: Int,
    val name: String
) : Parcelable