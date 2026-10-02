/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.cache

fun movieDetailsCacheKey(movieId: Int, language: String): String {
    return "movie:$movieId:language:$language"
}

fun personDetailsCacheKey(personId: Int, language: String): String {
    return "person:$personId:language:$language"
}
