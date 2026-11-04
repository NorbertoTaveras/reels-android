/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.account.usecase

import com.norbertotaveras.reels.domain.account.model.TmdbLibraryList
import com.norbertotaveras.reels.domain.account.repository.TmdbAccountRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTmdbLibraryLastSyncedAtUseCase @Inject constructor(
    private val repository: TmdbAccountRepository,
) {
    operator fun invoke(
        list: TmdbLibraryList,
        language: String,
    ): Flow<Long?> {
        return repository.observeLibraryLastSyncedAt(
            list = list,
            language = language
        )
    }
}
