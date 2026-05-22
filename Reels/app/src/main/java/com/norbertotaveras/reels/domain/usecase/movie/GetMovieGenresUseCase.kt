package com.norbertotaveras.reels.domain.usecase.movie

import com.norbertotaveras.reels.domain.model.genre.GenreDataModel
import com.norbertotaveras.reels.domain.repository.movie.MovieRepository
import javax.inject.Inject

class GetMovieGenresUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        language: String = "en-US"
    ): Result<List<GenreDataModel>> {
        return repository.getMovieGenres(language = language)
    }
}
