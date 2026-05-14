package com.norbertotaveras.reels.usecases.movie

import com.norbertotaveras.reels.model.data.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.network.TMDBFwkRepository
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(
    private val repository: TMDBFwkRepository
) {
    suspend operator fun invoke(
        movieId: Int,
        language: String = "en-US"
    ): Result<MovieDetailsDataModel> {
        return runCatching {
            repository.getMovieDetails(
                movieId = movieId,
                language = language
            ).toMovieDetailsDataModel()
        }
    }
}
