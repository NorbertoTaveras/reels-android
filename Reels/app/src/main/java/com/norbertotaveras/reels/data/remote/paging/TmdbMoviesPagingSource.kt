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
import com.norbertotaveras.reels.data.remote.dto.movie.MoviesResponseDto
import com.norbertotaveras.reels.data.remote.error.toTmdbNetworkException
import kotlinx.coroutines.CancellationException

internal class TmdbMoviesPagingSource(
    private val loadPage: suspend (page: Int) -> MoviesResponseDto,
    private val transformError: suspend (error: Throwable) -> Throwable = { error -> error },
) : PagingSource<Int, MovieDto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieDto> {
        return try {
            val page = params.key ?: TMDB_FIRST_PAGE
            val response = loadPage(page)

            LoadResult.Page(
                data = response.results,
                prevKey = if (page == TMDB_FIRST_PAGE) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            LoadResult.Error(transformError(error).toTmdbNetworkException())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieDto>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition)
        return anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }
}
