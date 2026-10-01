/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.norbertotaveras.reels.data.remote.dto.movie.MovieDto
import com.norbertotaveras.reels.data.remote.error.toTmdbNetworkException
import com.norbertotaveras.reels.data.remote.mapper.movie.toDiscoverMovieRequestParameters
import com.norbertotaveras.reels.data.remote.movie.TmdbMovieRemoteDataSource
import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel

class DiscoverMoviesPagingSource(
    private val remoteDataSource: TmdbMovieRemoteDataSource,
    filters: DiscoverFilterDataModel,
    private val language: String,
) : PagingSource<Int, MovieDto>() {
    private val requestParameters = filters.toDiscoverMovieRequestParameters()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieDto> {
        return try {
            val page = params.key ?: TMDB_FIRST_PAGE
            val response = remoteDataSource.discoverMovies(
                language = language,
                page = page,
                parameters = requestParameters
            )

            LoadResult.Page(
                data = response.results,
                prevKey = if (page == TMDB_FIRST_PAGE) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (error: Exception) {
            LoadResult.Error(error.toTmdbNetworkException())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieDto>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition)
        return anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }
}
