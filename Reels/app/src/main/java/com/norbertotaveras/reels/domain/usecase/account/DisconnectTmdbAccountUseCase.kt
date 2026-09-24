/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.account

import com.norbertotaveras.reels.domain.repository.account.TmdbAccountRepository
import javax.inject.Inject

class DisconnectTmdbAccountUseCase @Inject constructor(
    private val repository: TmdbAccountRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.disconnect()
    }
}
