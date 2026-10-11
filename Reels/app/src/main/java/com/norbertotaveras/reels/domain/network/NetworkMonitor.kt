/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.network

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val connectivity: Flow<Boolean>

    fun isOnline(): Boolean
}
