package com.norbertotaveras.reels.domain.usecase.movie

import com.norbertotaveras.reels.domain.model.movie.MovieVideosDataModel
import com.norbertotaveras.reels.data.remote.TMDBFwkRepository
import javax.inject.Inject

class GetMovieVideosUseCase @Inject constructor(
    private val repository: TMDBFwkRepository
) {
    suspend operator fun invoke(
        movieId: Int,
        language: String = "en-US"
    ): Result<MovieVideosDataModel> {
        return runCatching {
            repository.getMovieVideos(
                movieId = movieId,
                language = language
            ).toMovieVideosDataModel()
        }
    }
}
