/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.repository.account

import com.norbertotaveras.reels.domain.model.account.TmdbAccountStateDataModel
import kotlinx.coroutines.flow.Flow

interface TmdbAccountRepository {
    fun observeAccountState(): Flow<TmdbAccountStateDataModel>

    suspend fun startAuthentication(): Result<String>

    suspend fun finishAuthentication(): Result<TmdbAccountStateDataModel>

    suspend fun disconnect(): Result<Unit>
}
