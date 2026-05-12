package com.norbertotaveras.reels.usecases

import com.norbertotaveras.reels.model.data.PersonDetailsDataModel
import com.norbertotaveras.reels.network.TMDBFwkRepository
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
