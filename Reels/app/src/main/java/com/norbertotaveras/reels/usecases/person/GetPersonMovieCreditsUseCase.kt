package com.norbertotaveras.reels.usecases.person

import com.norbertotaveras.reels.model.data.person.PersonMovieCreditsDataModel
import com.norbertotaveras.reels.network.TMDBFwkRepository
import javax.inject.Inject

class GetPersonMovieCreditsUseCase @Inject constructor(
    private val repository: TMDBFwkRepository
) {
    suspend operator fun invoke(
        personId: Int,
        language: String = "en-US"
    ): Result<PersonMovieCreditsDataModel> {
        return runCatching {
            repository.getPersonMovieCredits(
                personId = personId,
                language = language
            ).toPersonMovieCreditsDataModel()
        }
    }
}
