/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.widget

import android.util.Log
import com.norbertotaveras.reels.domain.network.NetworkMonitor
import com.norbertotaveras.reels.domain.plans.ReelsWidgetUpdater
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.distinctUntilChanged

@Singleton
class WidgetConnectivityObserver @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val widgetUpdater: ReelsWidgetUpdater,
) {
    suspend fun observe() {
        // Glance stops collecting after its session ends; refresh independently of that session.
        networkMonitor.connectivity.distinctUntilChanged().collect {
            try {
                widgetUpdater.updateAll()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.w(TAG, "Unable to refresh widgets after a connectivity change", exception)
            }
        }
    }

    private companion object {
        const val TAG = "WidgetConnectivity"
    }
}
