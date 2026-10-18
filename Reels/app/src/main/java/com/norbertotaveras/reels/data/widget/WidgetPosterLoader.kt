/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.norbertotaveras.reels.BuildConfig
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object WidgetPosterLoader {
    suspend fun load(context: Context, posterPath: String?): Bitmap? = withContext(Dispatchers.IO) {
        if (posterPath.isNullOrBlank()) return@withContext null
        val cacheFile = File(context.cacheDir, "widget-${posterPath.hashCode()}.jpg")
        if (cacheFile.exists()) {
            BitmapFactory.decodeFile(cacheFile.absolutePath)?.let { return@withContext it }
        }
        runCatching {
            val connection = URL("${BuildConfig.BASE_IMAGE_URL}w342$posterPath").openConnection() as HttpURLConnection
            connection.connectTimeout = NETWORK_TIMEOUT_MILLIS
            connection.readTimeout = NETWORK_TIMEOUT_MILLIS
            connection.inputStream.use { input ->
                cacheFile.outputStream().use(input::copyTo)
            }
            BitmapFactory.decodeFile(cacheFile.absolutePath)
        }.getOrNull()
    }

    private const val NETWORK_TIMEOUT_MILLIS = 5_000
}
