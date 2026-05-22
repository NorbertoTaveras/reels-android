package com.norbertotaveras.reels.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.norbertotaveras.reels.domain.model.constant.MovieCategory
import com.norbertotaveras.reels.data.remote.dto.movie.MovieDto
import com.norbertotaveras.reels.data.remote.dto.movie.MoviesResponseDto
import com.norbertotaveras.reels.data.remote.TmdbRemoteDataSource

class MoviesPagingSource(
    private val remoteDataSource: TmdbRemoteDataSource,
    private val category: MovieCategory,
    private val language: String,
) : PagingSource<Int, MovieDto>() {

    companion object {
        private const val TAG = "MoviesPagingSource"
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieDto> {
        return try {
            val page = params.key ?: 1

            val response: MoviesResponseDto = remoteDataSource.getMovies(
                category = category.value,
                language = language,
                page = page
            )

            LoadResult.Page(
                data = response.results,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieDto>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition)
        return anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }
}
