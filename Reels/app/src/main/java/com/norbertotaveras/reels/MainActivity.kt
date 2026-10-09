/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.norbertotaveras.reels.domain.model.settings.AppSettings
import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.account.TmdbAuthenticationCallback
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import com.norbertotaveras.reels.presentation.navigation.ReelsNavHost
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var observeSettingsUseCase: ObserveSettingsUseCase

    private var tmdbAuthCallbackId by mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            val settings by observeSettingsUseCase().collectAsStateWithLifecycle(
                initialValue = AppSettings()
            )
            val systemInDarkTheme = isSystemInDarkTheme()
            val darkTheme = when (settings.theme) {
                AppTheme.System -> systemInDarkTheme
                AppTheme.Dark -> true
                AppTheme.Light -> false
            }

            ReelsTheme(
                darkTheme = darkTheme,
                imageQuality = settings.imageQuality
            ) {
                ReelsNavHost(
                    modifier = Modifier.fillMaxSize(),
                    tmdbAuthCallbackId = tmdbAuthCallbackId,
                    onTmdbAuthCallbackConsumed = { callbackId ->
                        if (tmdbAuthCallbackId == callbackId) {
                            tmdbAuthCallbackId = null
                        }
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val callbackUri = intent?.data ?: return
        val isTmdbCallback = callbackUri.scheme == TmdbAuthenticationCallback.SCHEME &&
            callbackUri.host == TmdbAuthenticationCallback.HOST &&
            callbackUri.path == TmdbAuthenticationCallback.PATH

        if (isTmdbCallback) {
            tmdbAuthCallbackId = SystemClock.elapsedRealtimeNanos()
        }
    }
}
