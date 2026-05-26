package com.norbertotaveras.reels.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.norbertotaveras.reels.data.remote.TmdbRemoteDataSource
import com.norbertotaveras.reels.data.remote.error.toTmdbNetworkException
import com.norbertotaveras.reels.data.remote.paging.MoviesPagingSource
import com.norbertotaveras.reels.data.remote.paging.SearchMoviesPagingSource
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class TmdbRepositoryImpl @Inject constructor(
    private val remoteDataSource: TmdbRemoteDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
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
        return tmdbResult {
            remoteDataSource.getMovieDetails(
                movieId = movieId,
                language = language
            ).toMovieDetailsDataModel()
        }
    }

    override suspend fun getMovieCredits(
        movieId: Int,
        language: String,
    ): Result<MovieCreditsDataModel> {
        return tmdbResult {
            remoteDataSource.getMovieCredits(
                movieId = movieId,
                language = language
            ).toMovieCreditsDataModel()
        }
    }

    override suspend fun getMovieVideos(
        movieId: Int,
        language: String,
    ): Result<MovieVideosDataModel> {
        return tmdbResult {
            remoteDataSource.getMovieVideos(
                movieId = movieId,
                language = language
            ).toMovieVideosDataModel()
        }
    }

    override suspend fun getSimilarMovies(
        movieId: Int,
        language: String,
        page: Int,
    ): Result<List<MovieDataModel>> {
        return tmdbResult {
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
        return tmdbResult {
            remoteDataSource.getMovieGenres(
                language = language
            ).genres?.map { genreDto -> genreDto.toGenreDataModel() } ?: emptyList()
        }
    }

    override suspend fun getPersonDetails(
        personId: Int,
        language: String,
    ): Result<PersonDetailsDataModel> {
        return tmdbResult {
            remoteDataSource.getPersonDetails(
                personId = personId,
                language = language
            ).toPersonDetailsDataModel()
        }
    }

    override suspend fun getPersonMovieCredits(
        personId: Int,
        language: String,
    ): Result<PersonMovieCreditsDataModel> {
        return tmdbResult {
            remoteDataSource.getPersonMovieCredits(
                personId = personId,
                language = language
            ).toPersonMovieCreditsDataModel()
        }
    }

    private suspend fun <T> tmdbResult(block: suspend () -> T): Result<T> {
        return runCatching {
            withContext(ioDispatcher) {
                block()
            }
        }.fold(
            onSuccess = { value -> Result.success(value) },
            onFailure = { error -> Result.failure(error.toTmdbNetworkException()) }
        )
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
