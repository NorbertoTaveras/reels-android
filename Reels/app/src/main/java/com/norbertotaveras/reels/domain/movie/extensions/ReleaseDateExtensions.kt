/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.movie.extensions

import java.time.LocalDate

fun String?.toConfirmedReleaseDate(): String? {
    val value = this?.takeIf { it.length == 10 } ?: return null
    return runCatching { LocalDate.parse(value).toString() }.getOrNull()
}
