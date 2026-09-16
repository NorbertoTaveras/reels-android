package com.norbertotaveras.reels.domain.usecase.favorites

import com.norbertotaveras.reels.domain.repository.favorites.FavoriteMoviesRepository
import javax.inject.Inject

class RemoveFavoriteMovieUseCase @Inject constructor(
    private val repository: FavoriteMoviesRepository
) {
    suspend operator fun invoke(movieId: Int): Result<Unit> {
        return runCatching {
            repository.removeFavoriteMovie(movieId = movieId)
        }
    }
}
