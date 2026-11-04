/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.settings.usecase

import com.norbertotaveras.reels.domain.settings.model.ImageQuality
import com.norbertotaveras.reels.domain.settings.repository.SettingsRepository
import javax.inject.Inject

class UpdateImageQualityUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(imageQuality: ImageQuality) {
        repository.updateImageQuality(imageQuality = imageQuality)
    }
}
