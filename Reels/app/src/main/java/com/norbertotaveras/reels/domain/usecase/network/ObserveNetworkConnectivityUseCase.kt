/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.network

import com.norbertotaveras.reels.domain.network.NetworkMonitor
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveNetworkConnectivityUseCase @Inject constructor(
    private val networkMonitor: NetworkMonitor,
) {
    operator fun invoke(): Flow<Boolean> = networkMonitor.connectivity
}
