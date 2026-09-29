/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote

import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel

internal fun DiscoverFilterDataModel.releaseDateFromParameter(): String? {
    return releaseYearFrom?.let { year -> "$year-01-01" }
}

internal fun DiscoverFilterDataModel.releaseDateToParameter(): String? {
    return releaseYearTo?.let { year -> "$year-12-31" }
}
