package com.norbertotaveras.reels.usecases.movie

import com.norbertotaveras.reels.model.data.genre.GenreDataModel
import com.norbertotaveras.reels.network.TMDBFwkRepository
import javax.inject.Inject

class GetMovieGenresUseCase @Inject constructor(
    private val repository: TMDBFwkRepository
) {
    suspend operator fun invoke(
        language: String = "en-US"
    ): Result<List<GenreDataModel>> {
        return runCatching {
            repository.getMovieGenres(
                language = language
            ).toGenreDataModels()
        }
    }
}
