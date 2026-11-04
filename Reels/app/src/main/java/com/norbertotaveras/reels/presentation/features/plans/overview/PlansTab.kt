/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans.overview

enum class PlansTab(val routeValue: String, val label: String) {
    Releases(routeValue = "releases", label = "Releases"),
    MovieNights(routeValue = "movie_nights", label = "Movie Nights"),
    ;

    companion object {
        fun fromRoute(value: String?): PlansTab = entries.firstOrNull {
            it.routeValue == value
        } ?: Releases
    }
}
