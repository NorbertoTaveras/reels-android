package com.norbertotaveras.reels.domain.usecase.movie

import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.repository.movie.MovieRepository
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        movieId: Int,
        language: String = "en-US"
    ): Result<MovieDetailsDataModel> {
        return repository.getMovieDetails(
            movieId = movieId,
            language = language
        )
    }
}
