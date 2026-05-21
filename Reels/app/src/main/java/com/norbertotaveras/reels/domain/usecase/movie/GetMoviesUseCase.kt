package com.norbertotaveras.reels.domain.usecase.movie

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.norbertotaveras.reels.domain.model.constant.MovieCategory
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.data.remote.TMDBFwkRepository
import com.norbertotaveras.reels.data.remote.paging.MoviesPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMoviesUseCase @Inject constructor(
    private val repository: TMDBFwkRepository
) {
    operator fun invoke(
        category: MovieCategory,
        language: String,
    ): Flow<PagingData<MovieDataModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MoviesPagingSource(
                    repository = repository,
                    category = category,
                    language = language
                )
            }
        ).flow.map {
            it.map { it.toMovieDataModel() }
        }
    }
}
