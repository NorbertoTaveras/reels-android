/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.norbertotaveras.reels.domain.network.NetworkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class AndroidNetworkMonitor @Inject constructor(
    @ApplicationContext context: Context,
) : NetworkMonitor {
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private val validatedNetworks = ConcurrentHashMap.newKeySet<Network>()
    private val onlineState = MutableStateFlow(currentConnectivity())
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateNetwork(
                network = network,
                capabilities = connectivityManager.getNetworkCapabilities(network)
            )
        }

        override fun onLost(network: Network) {
            validatedNetworks.remove(network)
            updateConnectivity()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities,
        ) {
            updateNetwork(network = network, capabilities = networkCapabilities)
        }
    }

    override val connectivity: Flow<Boolean> = onlineState.asStateFlow()

    init {
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build(),
            networkCallback
        )
    }

    override fun isOnline(): Boolean = currentConnectivity()

    private fun updateNetwork(
        network: Network,
        capabilities: NetworkCapabilities?,
    ) {
        if (NetworkCapabilitiesHelper.hasValidatedInternet(capabilities)) {
            validatedNetworks.add(network)
        } else {
            validatedNetworks.remove(network)
        }
        updateConnectivity()
    }

    private fun updateConnectivity() {
        onlineState.value = validatedNetworks.isNotEmpty()
    }

    private fun currentConnectivity(): Boolean {
        return NetworkCapabilitiesHelper.hasValidatedInternet(
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        )
    }
}
