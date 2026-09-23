/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.settings

import com.norbertotaveras.reels.domain.model.settings.AppSettings
import com.norbertotaveras.reels.domain.repository.settings.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<AppSettings> {
        return repository.observeSettings()
    }
}
