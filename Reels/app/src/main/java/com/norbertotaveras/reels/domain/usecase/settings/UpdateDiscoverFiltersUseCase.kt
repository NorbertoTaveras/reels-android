/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.settings

import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.repository.settings.SettingsRepository
import javax.inject.Inject

class UpdateDiscoverFiltersUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(filters: DiscoverFilterDataModel) {
        repository.updateDiscoverFilters(filters = filters)
    }
}
