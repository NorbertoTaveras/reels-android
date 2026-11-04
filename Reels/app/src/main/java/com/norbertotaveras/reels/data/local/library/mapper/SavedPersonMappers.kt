/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.library.mapper

import com.norbertotaveras.reels.data.local.library.entity.SavedPersonEntity
import com.norbertotaveras.reels.data.local.library.entity.SavedPersonKnownForEntity
import com.norbertotaveras.reels.data.local.library.entity.SavedPersonWithKnownFor
import com.norbertotaveras.reels.domain.library.model.SavedPersonDataModel
import com.norbertotaveras.reels.domain.person.model.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.person.model.PersonMovieCreditDataModel

fun SavedPersonWithKnownFor.toSavedPersonDataModel(): SavedPersonDataModel {
    return SavedPersonDataModel(
        id = person.id,
        name = person.name,
        biography = person.biography,
        birthday = person.birthday,
        placeOfBirth = person.placeOfBirth,
        profilePath = person.profilePath,
        knownForDepartment = person.knownForDepartment,
        knownFor = knownFor
            .sortedBy { movie -> movie.position }
            .map { movie -> movie.toPersonMovieCreditDataModel() },
        savedAt = person.savedAt
    )
}

fun PersonDetailsDataModel.toSavedPersonEntity(
    savedAt: Long
): SavedPersonEntity {
    return SavedPersonEntity(
        id = id,
        name = name,
        biography = biography,
        birthday = birthday,
        placeOfBirth = placeOfBirth,
        profilePath = profilePath,
        knownForDepartment = knownForDepartment,
        savedAt = savedAt
    )
}

fun PersonMovieCreditDataModel.toSavedPersonKnownForEntity(
    personId: Int,
    position: Int
): SavedPersonKnownForEntity {
    return SavedPersonKnownForEntity(
        personId = personId,
        movieId = id,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate,
        character = character,
        voteAverage = voteAverage,
        popularity = popularity,
        position = position
    )
}

private fun SavedPersonKnownForEntity.toPersonMovieCreditDataModel(): PersonMovieCreditDataModel {
    return PersonMovieCreditDataModel(
        id = movieId,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate,
        character = character,
        voteAverage = voteAverage,
        popularity = popularity
    )
}
