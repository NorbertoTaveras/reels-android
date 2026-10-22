/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.di.app

import android.app.Application
import com.norbertotaveras.reels.data.widget.WidgetConnectivityObserver
import com.norbertotaveras.reels.domain.plans.ReelsNotificationManager
import com.norbertotaveras.reels.domain.repository.settings.SettingsRepository
import com.norbertotaveras.reels.domain.usecase.plans.RefreshTrackedReleasesUseCase
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltAndroidApp
class ReelsApplication : Application() {
    @Inject lateinit var notificationManager: ReelsNotificationManager
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var refreshTrackedReleases: RefreshTrackedReleasesUseCase
    @Inject lateinit var widgetConnectivityObserver: WidgetConnectivityObserver
    @Inject @IoDispatcher lateinit var ioDispatcher: CoroutineDispatcher

    override fun onCreate() {
        super.onCreate()
        notificationManager.createChannels()
        val applicationScope = CoroutineScope(SupervisorJob() + ioDispatcher)
        applicationScope.launch {
            widgetConnectivityObserver.observe()
        }
        applicationScope.launch {
            val language = settingsRepository.observeSettings().first().languageRegion.languageCode
            refreshTrackedReleases(language)
        }
    }
}
