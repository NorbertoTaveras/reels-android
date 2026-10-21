/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.widget

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.norbertotaveras.reels.data.widget.WidgetPosterLoader

internal data class WidgetArtwork<Key>(
    val posters: Map<Key, Bitmap?> = emptyMap(),
    val backdrop: Bitmap? = null,
)

@Composable
internal fun <Key> rememberWidgetArtwork(context: Context, paths: Map<Key, String?>): WidgetArtwork<Key> {
    val artwork by produceState(WidgetArtwork<Key>(), context, paths) {
        // Publish each image independently so a slow download cannot hold up plan state changes.
        val posters = mutableMapOf<Key, Bitmap?>()
        value = WidgetArtwork()
        paths.forEach { (id, path) ->
            posters[id] = WidgetPosterLoader.load(context, path)
            value = WidgetArtwork(
                posters = posters.toMap(),
                backdrop = WidgetPosterLoader.createBackdrop(posters[paths.keys.firstOrNull()]),
            )
        }
    }
    return artwork
}
