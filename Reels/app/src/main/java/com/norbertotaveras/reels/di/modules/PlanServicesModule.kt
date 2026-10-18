/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.di.modules

import com.norbertotaveras.reels.data.plans.AndroidPlanScheduler
import com.norbertotaveras.reels.data.plans.AndroidReelsNotificationManager
import com.norbertotaveras.reels.data.widget.AndroidReelsWidgetUpdater
import com.norbertotaveras.reels.domain.plans.PlanScheduler
import com.norbertotaveras.reels.domain.plans.ReelsNotificationManager
import com.norbertotaveras.reels.domain.plans.ReelsWidgetUpdater
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlanServicesModule {
    @Binds
    @Singleton
    abstract fun bindPlanScheduler(implementation: AndroidPlanScheduler): PlanScheduler

    @Binds
    @Singleton
    abstract fun bindNotificationManager(
        implementation: AndroidReelsNotificationManager,
    ): ReelsNotificationManager

    @Binds
    @Singleton
    abstract fun bindWidgetUpdater(implementation: AndroidReelsWidgetUpdater): ReelsWidgetUpdater
}
