/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository.settings

import android.content.Context
import coil3.SingletonImageLoader
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
import com.norbertotaveras.reels.domain.settings.repository.ImageCacheRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Singleton
class CoilImageCacheRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ImageCacheRepository {
    override suspend fun getSizeBytes(): Long {
        return withContext(ioDispatcher) {
            SingletonImageLoader.get(context).diskCache?.size ?: 0L
        }
    }

    override suspend fun clear(): Long {
        return withContext(ioDispatcher) {
            val imageLoader = SingletonImageLoader.get(context)
            val clearedBytes = imageLoader.diskCache?.size ?: 0L
            imageLoader.memoryCache?.clear()
            imageLoader.diskCache?.clear()
            clearedBytes
        }
    }
}
