package com.norbertotaveras.reels.usecases

import com.norbertotaveras.reels.model.data.MovieVideosDataModel
import com.norbertotaveras.reels.network.TMDBFwkRepository
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
