package com.norbertotaveras.reels.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieVideoDataModel(
    val id: String,
    val name: String,
    val key: String,
    val site: String,
    val type: String,
    val official: Boolean
) : Parcelable {
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
}
