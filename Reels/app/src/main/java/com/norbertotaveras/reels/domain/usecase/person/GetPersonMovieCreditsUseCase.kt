package com.norbertotaveras.reels.domain.usecase.person

import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditsDataModel
import com.norbertotaveras.reels.data.remote.TMDBFwkRepository
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
