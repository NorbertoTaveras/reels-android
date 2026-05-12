package com.norbertotaveras.reels.usecases

import com.norbertotaveras.reels.model.data.MovieCreditsDataModel
import com.norbertotaveras.reels.network.TMDBFwkRepository
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
