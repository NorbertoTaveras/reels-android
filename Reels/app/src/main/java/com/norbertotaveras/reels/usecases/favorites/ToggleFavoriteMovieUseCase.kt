package com.norbertotaveras.reels.usecases.favorites

import com.norbertotaveras.reels.model.data.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.repository.favorites.FavoriteMoviesRepository
import javax.inject.Inject

class ToggleFavoriteMovieUseCase @Inject constructor(
    private val repository: FavoriteMoviesRepository
) {
    suspend operator fun invoke(movie: MovieDetailsDataModel): Result<Unit> {
        return runCatching {
            repository.toggleFavoriteMovie(movie = movie)
        }
    }
}
