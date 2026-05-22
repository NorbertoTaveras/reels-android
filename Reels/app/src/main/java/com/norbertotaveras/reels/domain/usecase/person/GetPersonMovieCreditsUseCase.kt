package com.norbertotaveras.reels.domain.usecase.person

import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditsDataModel
import com.norbertotaveras.reels.domain.repository.person.PersonRepository
import javax.inject.Inject

class GetPersonMovieCreditsUseCase @Inject constructor(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(
        personId: Int,
        language: String = "en-US"
    ): Result<PersonMovieCreditsDataModel> {
        return repository.getPersonMovieCredits(
            personId = personId,
            language = language
        )
    }
}
