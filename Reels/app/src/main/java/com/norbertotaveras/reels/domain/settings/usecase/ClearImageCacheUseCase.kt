/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.settings.usecase

import com.norbertotaveras.reels.domain.settings.repository.ImageCacheRepository
import javax.inject.Inject

class ClearImageCacheUseCase @Inject constructor(
    private val repository: ImageCacheRepository,
) {
    suspend operator fun invoke(): Long = repository.clear()
}
