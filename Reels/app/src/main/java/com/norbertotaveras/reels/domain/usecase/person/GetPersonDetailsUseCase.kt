package com.norbertotaveras.reels.domain.usecase.person

import com.norbertotaveras.reels.domain.model.person.PersonDetailsDataModel
import com.norbertotaveras.reels.data.remote.TMDBFwkRepository
import javax.inject.Inject

class GetPersonDetailsUseCase @Inject constructor(
    private val repository: TMDBFwkRepository
) {
    suspend operator fun invoke(
        personId: Int,
        language: String = "en-US"
    ): Result<PersonDetailsDataModel> {
        return runCatching {
            repository.getPersonDetails(
                personId = personId,
                language = language
            ).toPersonDetailsDataModel()
        }
    }
}
