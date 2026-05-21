package com.norbertotaveras.reels.domain.usecase.movie

import com.norbertotaveras.reels.domain.model.movie.MovieCreditsDataModel
import com.norbertotaveras.reels.data.remote.TMDBFwkRepository
import javax.inject.Inject

class GetMovieCreditsUseCase @Inject constructor(
    private val repository: TMDBFwkRepository
) {
    suspend operator fun invoke(
        movieId: Int,
        language: String = "en-US"
    ): Result<MovieCreditsDataModel> {
        /*return runCatching {
            repository.getMovieCredits(
                movieId = movieId,
                language = language
            ).toMovieCreditsDataModel()
        }*/
        return repository.runCatching {
            getMovieCredits(
                movieId = movieId,
                language = language
            )
        }.mapCatching {
            Result.success(it.toMovieCreditsDataModel())
        }.getOrElse { Result.failure(it) }
    }
}
