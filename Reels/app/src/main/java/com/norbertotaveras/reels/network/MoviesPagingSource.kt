package com.norbertotaveras.reels.network

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.norbertotaveras.reels.model.constant.MovieCategory
import com.norbertotaveras.reels.model.dto.MovieDto
import com.norbertotaveras.reels.model.dto.MoviesResponseDto

class MoviesPagingSource(
    private val repository: TMDBFwkRepository,
    private val category: MovieCategory,
    private val language: String,
) : PagingSource<Int, MovieDto>() {

    companion object {
        private const val TAG = "MoviesPagingSource"
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieDto> {
        return try {
            val page = params.key ?: 1
            Log.d(TAG, "Requesting page=$page category=${category.value} language=$language")

            val response: MoviesResponseDto = repository.getMovies(
                category = category.value,
                language = language,
                page = page
            )

            Log.d(
                TAG,
                "Loaded page=$page results=${response.results.size} totalPages=${response.totalPages}"
            )

            LoadResult.Page(
                data = response.results,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load page", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieDto>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition)
        return anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }
}
