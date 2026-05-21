package com.norbertotaveras.reels.domain.usecase.favorites

import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.repository.favorites.FavoriteMoviesRepository
import javax.inject.Inject

class GetFavoriteMovieDetailsUseCase @Inject constructor(
    private val repository: FavoriteMoviesRepository
) {
    suspend operator fun invoke(movieId: Int): MovieDetailsDataModel? {
        return repository.getFavoriteMovieDetails(movieId = movieId)
    }
}
