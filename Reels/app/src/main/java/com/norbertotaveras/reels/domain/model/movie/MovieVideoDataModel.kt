/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.movie


data class MovieVideoDataModel(
    val id: String,
    val name: String,
    val key: String,
    val site: String,
    val type: String,
    val official: Boolean
) {
    val youtubeThumbnailUrl: String?
        get() = if (site.equals("YouTube", ignoreCase = true)) {
            "https://img.youtube.com/vi/$key/hqdefault.jpg"
        } else {
            null
        }

    val youtubeUrl: String?
        get() = if (site.equals("YouTube", ignoreCase = true)) {
            "https://www.youtube.com/watch?v=$key"
        } else {
            null
        }

    val isPlayableTrailer: Boolean
        get() {
            val supportedTypes = setOf("Trailer", "Teaser")

            return site.equals("YouTube", ignoreCase = true) &&
                    supportedTypes.any { supportedType ->
                        type.equals(supportedType, ignoreCase = true)
                    }
        }

}
