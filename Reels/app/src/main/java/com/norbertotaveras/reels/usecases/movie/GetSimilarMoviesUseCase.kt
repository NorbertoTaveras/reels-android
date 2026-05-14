package com.norbertotaveras.reels.usecases.movie

import com.norbertotaveras.reels.model.data.movie.MovieDataModel
import com.norbertotaveras.reels.network.TMDBFwkRepository
import javax.inject.Inject

class GetSimilarMoviesUseCase @Inject constructor(
    private val repository: TMDBFwkRepository
) {
    suspend operator fun invoke(
        movieId: Int,
        language: String = "en-US",
        page: Int = 1
    ): Result<List<MovieDataModel>> {
        return runCatching {
            repository.getSimilarMovies(
                movieId = movieId,
                language = language,
                page = page
            ).results.map { movie ->
                movie.toMovieDataModel()
            }
        }
    }
}