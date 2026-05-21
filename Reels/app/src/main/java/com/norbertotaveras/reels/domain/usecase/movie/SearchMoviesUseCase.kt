package com.norbertotaveras.reels.domain.usecase.movie

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.data.remote.paging.SearchMoviesPagingSource
import com.norbertotaveras.reels.data.remote.TMDBFwkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val repository: TMDBFwkRepository
) {
    operator fun invoke(
        query: String,
        language: String = "en-US",
        includeAdult: Boolean = false
    ): Flow<PagingData<MovieDataModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchMoviesPagingSource(
                    repository = repository,
                    query = query,
                    language = language,
                    includeAdult = includeAdult
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { movieDto ->
                movieDto.toMovieDataModel()
            }
        }
    }
}