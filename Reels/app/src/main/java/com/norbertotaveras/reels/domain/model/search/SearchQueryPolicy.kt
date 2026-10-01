/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.search

object SearchQueryPolicy {
    const val MIN_LENGTH = 2

    fun normalize(query: String): String = query.trim()

    fun isValid(query: String): Boolean = normalize(query).length >= MIN_LENGTH
}
