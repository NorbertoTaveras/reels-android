package com.norbertotaveras.reels.domain.usecase.movie

import com.norbertotaveras.reels.domain.model.movie.MovieVideosDataModel
import com.norbertotaveras.reels.domain.repository.movie.MovieRepository
import javax.inject.Inject

class GetMovieVideosUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        movieId: Int,
        language: String = "en-US"
    ): Result<MovieVideosDataModel> {
        return repository.getMovieVideos(
            movieId = movieId,
            language = language
        )
    }
}
