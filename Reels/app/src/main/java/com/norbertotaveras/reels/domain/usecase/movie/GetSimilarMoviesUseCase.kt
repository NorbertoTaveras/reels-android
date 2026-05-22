package com.norbertotaveras.reels.domain.usecase.movie

import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.repository.movie.MovieRepository
import javax.inject.Inject

class GetSimilarMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        movieId: Int,
        language: String = "en-US",
        page: Int = 1
    ): Result<List<MovieDataModel>> {
        return repository.getSimilarMovies(
            movieId = movieId,
            language = language,
            page = page
        )
    }
}
