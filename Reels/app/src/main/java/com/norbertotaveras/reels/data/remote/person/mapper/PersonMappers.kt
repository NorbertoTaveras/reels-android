/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.person.mapper

import com.norbertotaveras.reels.data.remote.person.dto.PersonDetailsDto
import com.norbertotaveras.reels.data.remote.person.dto.PersonMovieCreditDto
import com.norbertotaveras.reels.data.remote.person.dto.PersonMovieCreditsDto
import com.norbertotaveras.reels.domain.person.model.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.person.model.PersonMovieCreditDataModel
import com.norbertotaveras.reels.domain.person.model.PersonMovieCreditsDataModel

fun PersonDetailsDto.toPersonDetailsDataModel(): PersonDetailsDataModel {
    return PersonDetailsDataModel(
        id = id,
        name = name,
        biography = biography.orEmpty(),
        birthday = birthday,
        placeOfBirth = placeOfBirth,
        profilePath = profilePath,
        knownForDepartment = knownForDepartment
    )
}

fun PersonMovieCreditsDto.toPersonMovieCreditsDataModel(): PersonMovieCreditsDataModel {
    return PersonMovieCreditsDataModel(
        cast = cast
            .orEmpty()
            .filter { credit -> credit.id != null && !credit.title.isNullOrBlank() }
            .map { credit -> credit.toPersonMovieCreditDataModel() }
            .sortedByDescending { credit -> credit.popularity }
    )
}

private fun PersonMovieCreditDto.toPersonMovieCreditDataModel(): PersonMovieCreditDataModel {
    return PersonMovieCreditDataModel(
        id = requireNotNull(id),
        title = title.orEmpty(),
        posterPath = posterPath,
        releaseDate = releaseDate,
        character = character,
        voteAverage = voteAverage ?: 0.0,
        popularity = popularity ?: 0.0
    )
}
