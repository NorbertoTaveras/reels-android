package com.norbertotaveras.reels.usecases.favorites

import com.norbertotaveras.reels.model.data.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.repository.favorites.FavoriteMoviesRepository
import javax.inject.Inject

class GetFavoriteMovieDetailsUseCase @Inject constructor(
    private val repository: FavoriteMoviesRepository
) {
    suspend operator fun invoke(movieId: Int): MovieDetailsDataModel? {
        return repository.getFavoriteMovieDetails(movieId = movieId)
    }
}
