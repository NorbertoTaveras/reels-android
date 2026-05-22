package com.norbertotaveras.reels.domain.usecase.movie

import com.norbertotaveras.reels.domain.model.movie.MovieCreditsDataModel
import com.norbertotaveras.reels.domain.repository.movie.MovieRepository
import javax.inject.Inject

class GetMovieCreditsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        movieId: Int,
        language: String = "en-US"
    ): Result<MovieCreditsDataModel> {
        return repository.getMovieCredits(
            movieId = movieId,
            language = language
        )
    }
}
