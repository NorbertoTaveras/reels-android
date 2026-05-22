package com.norbertotaveras.reels.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.norbertotaveras.reels.data.remote.TmdbRemoteDataSource
import com.norbertotaveras.reels.data.remote.paging.MoviesPagingSource
import com.norbertotaveras.reels.data.remote.paging.SearchMoviesPagingSource
import com.norbertotaveras.reels.domain.model.constant.MovieCategory
import com.norbertotaveras.reels.domain.model.genre.GenreDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieCreditsDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieVideosDataModel
import com.norbertotaveras.reels.domain.model.person.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditsDataModel
import com.norbertotaveras.reels.domain.repository.movie.MovieRepository
import com.norbertotaveras.reels.domain.repository.person.PersonRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class TmdbRepositoryImpl @Inject constructor(
    private val remoteDataSource: TmdbRemoteDataSource,
) : MovieRepository, PersonRepository {
    override fun getMovies(
        category: MovieCategory,
        language: String,
    ): Flow<PagingData<MovieDataModel>> {
        return Pager(
            config = defaultPagingConfig(),
            pagingSourceFactory = {
                MoviesPagingSource(
                    remoteDataSource = remoteDataSource,
                    category = category,
                    language = language
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { movieDto -> movieDto.toMovieDataModel() }
        }
    }

    override fun searchMovies(
        query: String,
        language: String,
        includeAdult: Boolean,
    ): Flow<PagingData<MovieDataModel>> {
        return Pager(
            config = defaultPagingConfig(),
            pagingSourceFactory = {
                SearchMoviesPagingSource(
                    remoteDataSource = remoteDataSource,
                    query = query,
                    language = language,
                    includeAdult = includeAdult
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { movieDto -> movieDto.toMovieDataModel() }
        }
    }

    override suspend fun getMovieDetails(
        movieId: Int,
        language: String,
    ): Result<MovieDetailsDataModel> {
        return runCatching {
            remoteDataSource.getMovieDetails(
                movieId = movieId,
                language = language
            )
        }.mapCatching {
            Result.success(it.toMovieDetailsDataModel())
        }.getOrElse {
            Result.failure(it)
        }
    }

    override suspend fun getMovieCredits(
        movieId: Int,
        language: String,
    ): Result<MovieCreditsDataModel> {
        return runCatching {
            remoteDataSource.getMovieCredits(
                movieId = movieId,
                language = language
            )
        }.mapCatching {
            Result.success(it.toMovieCreditsDataModel())
        }.getOrElse {
            Result.failure(it)
        }
    }

    override suspend fun getMovieVideos(
        movieId: Int,
        language: String,
    ): Result<MovieVideosDataModel> {
        return runCatching {
            remoteDataSource.getMovieVideos(
                movieId = movieId,
                language = language
            )
        }.mapCatching {
            Result.success(it.toMovieVideosDataModel())
        }.getOrElse {
            Result.failure(it)
        }
    }

    override suspend fun getSimilarMovies(
        movieId: Int,
        language: String,
        page: Int,
    ): Result<List<MovieDataModel>> {
        return runCatching {
            remoteDataSource.getSimilarMovies(
                movieId = movieId,
                language = language,
                page = page
            ).results.map {
                movieDto -> movieDto.toMovieDataModel()
            }
        }
    }

    override suspend fun getMovieGenres(
        language: String,
    ): Result<List<GenreDataModel>> {
        return runCatching {
            remoteDataSource.getMovieGenres(
                language = language
            )
        }.mapCatching {
            Result.success(it.genres?.map { genreDto -> genreDto.toGenreDataModel() } ?: emptyList() )
        }.getOrElse {
            Result.failure(it)
        }
    }

    override suspend fun getPersonDetails(
        personId: Int,
        language: String,
    ): Result<PersonDetailsDataModel> {
        return runCatching {
            remoteDataSource.getPersonDetails(
                personId = personId,
                language = language
            )
        }.mapCatching {
            Result.success(it.toPersonDetailsDataModel())
        }.getOrElse {
            Result.failure(it)
        }
    }

    override suspend fun getPersonMovieCredits(
        personId: Int,
        language: String,
    ): Result<PersonMovieCreditsDataModel> {
        return runCatching {
            remoteDataSource.getPersonMovieCredits(
                personId = personId,
                language = language
            )
        }.mapCatching {
            Result.success(it.toPersonMovieCreditsDataModel())
        }.getOrElse {
            Result.failure(it)
        }
    }

    private fun defaultPagingConfig(): PagingConfig {
        return PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            prefetchDistance = 5,
            enablePlaceholders = false
        )
    }
}
