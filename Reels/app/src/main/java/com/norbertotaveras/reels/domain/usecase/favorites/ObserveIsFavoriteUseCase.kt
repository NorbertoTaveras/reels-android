package com.norbertotaveras.reels.domain.usecase.favorites

import com.norbertotaveras.reels.domain.repository.favorites.FavoriteMoviesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveIsFavoriteUseCase @Inject constructor(
    private val repository: FavoriteMoviesRepository
) {
    operator fun invoke(movieId: Int): Flow<Boolean> {
        return repository.observeIsFavorite(movieId = movieId)
    }
}
