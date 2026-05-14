package com.norbertotaveras.reels.model.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.norbertotaveras.reels.model.dto.movie.MovieDto
import com.norbertotaveras.reels.network.TMDBFwkRepository

class SearchMoviesPagingSource(
    private val repository: TMDBFwkRepository,
    private val query: String,
    private val language: String,
    private val includeAdult: Boolean = false
) : PagingSource<Int, MovieDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieDto> {
        return try {
            val page = params.key ?: 1

            val response = repository.searchMovies(
                query = query,
                language = language,
                page = page,
                includeAdult = includeAdult
            )

            LoadResult.Page(
                data = response.results,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (error: Exception) {
            LoadResult.Error(error)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val closestPage = state.closestPageToPosition(anchorPosition)

            closestPage?.prevKey?.plus(1)
                ?: closestPage?.nextKey?.minus(1)
        }
    }
}
