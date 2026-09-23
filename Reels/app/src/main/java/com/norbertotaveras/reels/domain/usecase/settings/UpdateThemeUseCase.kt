/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.settings

import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.repository.settings.SettingsRepository
import javax.inject.Inject

class UpdateThemeUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(theme: AppTheme) {
        repository.updateTheme(theme = theme)
    }
}
