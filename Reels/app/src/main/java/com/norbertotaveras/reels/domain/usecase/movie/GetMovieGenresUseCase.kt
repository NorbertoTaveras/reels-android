package com.norbertotaveras.reels.domain.usecase.movie

import com.norbertotaveras.reels.domain.model.genre.GenreDataModel
import com.norbertotaveras.reels.data.remote.TMDBFwkRepository
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
