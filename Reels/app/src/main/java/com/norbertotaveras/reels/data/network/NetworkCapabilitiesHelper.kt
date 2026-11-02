/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.network

import android.net.NetworkCapabilities
import android.os.Build

internal object NetworkCapabilitiesHelper {
    fun hasUsableInternet(capabilities: NetworkCapabilities?): Boolean {
        return capabilities?.run {
            hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.P ||
                    hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED))
        } == true
    }
}
