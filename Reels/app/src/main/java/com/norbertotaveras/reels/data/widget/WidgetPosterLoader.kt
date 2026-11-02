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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import com.norbertotaveras.reels.BuildConfig
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.graphics.createBitmap

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

    fun createBackdrop(source: Bitmap?, width: Int = 720, height: Int = 204): Bitmap? {
        source ?: return null
        val output = createBitmap(width, height)
        val canvas = Canvas(output)
        val scale = maxOf(width.toFloat() / source.width, height.toFloat() / source.height)
        val scaledWidth = (source.width * scale).toInt()
        val scaledHeight = (source.height * scale).toInt()
        val left = (width - scaledWidth) / 2
        canvas.drawBitmap(
            source,
            null,
            Rect(left, 0, left + scaledWidth, scaledHeight),
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG),
        )
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), Paint().apply {
            shader = LinearGradient(
                0f, height * BACKDROP_GRADIENT_START, 0f, height.toFloat(),
                Color.TRANSPARENT, Color.argb(BACKDROP_OVERLAY_ALPHA, 0, 0, 0),
                Shader.TileMode.CLAMP,
            )
        })
        return output
    }

    private const val NETWORK_TIMEOUT_MILLIS = 5_000
    private const val BACKDROP_OVERLAY_ALPHA = 102
    private const val BACKDROP_GRADIENT_START = 0.4f
}
