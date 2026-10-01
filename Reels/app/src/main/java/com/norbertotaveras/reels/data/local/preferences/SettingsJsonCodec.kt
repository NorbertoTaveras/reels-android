/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.preferences

import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.model.discover.DiscoverSort
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

internal object SettingsJsonCodec {
    fun decodeRecentSearches(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()

        return try {
            val jsonArray = JSONArray(value)
            buildList {
                repeat(jsonArray.length()) { index ->
                    jsonArray.optString(index)
                        .trim()
                        .takeIf { query -> query.isNotBlank() }
                        ?.let(::add)
                }
            }
        } catch (_: JSONException) {
            emptyList()
        }
    }

    fun encodeRecentSearches(recentSearches: List<String>): String {
        val jsonArray = JSONArray()
        recentSearches.forEach { query ->
            jsonArray.put(query)
        }
        return jsonArray.toString()
    }

    fun decodeDiscoverFilters(value: String?): DiscoverFilterDataModel {
        if (value.isNullOrBlank()) return DiscoverFilterDataModel()

        return try {
            val jsonObject = JSONObject(value)
            DiscoverFilterDataModel(
                sort = jsonObject.optString(DISCOVER_SORT)
                    .toEnumOrDefault(DiscoverSort.Popularity),
                genreId = jsonObject.optionalInt(DISCOVER_GENRE_ID),
                releaseYearFrom = jsonObject.optionalInt(DISCOVER_RELEASE_YEAR_FROM)
                    ?: jsonObject.optionalInt(LEGACY_DISCOVER_YEAR),
                releaseYearTo = jsonObject.optionalInt(DISCOVER_RELEASE_YEAR_TO)
                    ?: jsonObject.optionalInt(LEGACY_DISCOVER_YEAR),
                minimumRating = jsonObject.optionalInt(DISCOVER_MINIMUM_RATING)
            )
        } catch (_: JSONException) {
            DiscoverFilterDataModel()
        }
    }

    fun encodeDiscoverFilters(filters: DiscoverFilterDataModel): String {
        return JSONObject()
            .put(DISCOVER_SORT, filters.sort.name)
            .put(DISCOVER_GENRE_ID, filters.genreId)
            .put(DISCOVER_RELEASE_YEAR_FROM, filters.releaseYearFrom)
            .put(DISCOVER_RELEASE_YEAR_TO, filters.releaseYearTo)
            .put(DISCOVER_MINIMUM_RATING, filters.minimumRating)
            .toString()
    }

    private inline fun <reified T : Enum<T>> String?.toEnumOrDefault(default: T): T {
        return enumValues<T>().firstOrNull { value -> value.name == this } ?: default
    }

    private fun JSONObject.optionalInt(name: String): Int? {
        return if (isNull(name)) null else optInt(name)
    }

    private const val DISCOVER_SORT = "sort"
    private const val DISCOVER_GENRE_ID = "genreId"
    private const val LEGACY_DISCOVER_YEAR = "year"
    private const val DISCOVER_RELEASE_YEAR_FROM = "releaseYearFrom"
    private const val DISCOVER_RELEASE_YEAR_TO = "releaseYearTo"
    private const val DISCOVER_MINIMUM_RATING = "minimumRating"
}
