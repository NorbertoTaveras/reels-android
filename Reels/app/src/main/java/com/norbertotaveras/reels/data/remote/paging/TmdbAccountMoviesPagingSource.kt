/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.norbertotaveras.reels.data.remote.TmdbRemoteDataSource
import com.norbertotaveras.reels.data.remote.dto.movie.MovieDto
import com.norbertotaveras.reels.data.remote.error.TmdbSessionExpiredException
import com.norbertotaveras.reels.data.remote.error.isTmdbSessionExpiredError
import com.norbertotaveras.reels.data.remote.error.toTmdbNetworkException

class TmdbAccountMoviesPagingSource(
    private val remoteDataSource: TmdbRemoteDataSource,
    private val accountId: Int,
    private val sessionId: String,
    private val language: String,
    private val listType: TmdbAccountMovieListType = TmdbAccountMovieListType.Watchlist,
    private val onSessionExpired: suspend () -> Unit,
) : PagingSource<Int, MovieDto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieDto> {
        return try {
            val page = params.key ?: FIRST_PAGE
            val response = when (listType) {
                TmdbAccountMovieListType.Watchlist -> remoteDataSource.getWatchlistMovies(
                    accountId = accountId,
                    sessionId = sessionId,
                    language = language,
                    page = page
                )
                TmdbAccountMovieListType.Rated -> remoteDataSource.getRatedMovies(
                    accountId = accountId,
                    sessionId = sessionId,
                    language = language,
                    page = page
                )
            }

            LoadResult.Page(
                data = response.results,
                prevKey = if (page == FIRST_PAGE) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (error: Exception) {
            if (error.isTmdbSessionExpiredError()) {
                onSessionExpired()
                LoadResult.Error(TmdbSessionExpiredException().toTmdbNetworkException())
            } else {
                LoadResult.Error(error.toTmdbNetworkException())
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieDto>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition)
        return anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}
