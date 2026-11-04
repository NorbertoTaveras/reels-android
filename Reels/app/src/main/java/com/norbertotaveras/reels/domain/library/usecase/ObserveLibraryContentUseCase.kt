/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.library.usecase

import com.norbertotaveras.reels.domain.library.model.LibraryContentDataModel
import com.norbertotaveras.reels.domain.library.repository.FavoriteMoviesRepository
import com.norbertotaveras.reels.domain.library.repository.SavedPeopleRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveLibraryContentUseCase @Inject constructor(
    private val favoriteMoviesRepository: FavoriteMoviesRepository,
    private val savedPeopleRepository: SavedPeopleRepository,
) {
    operator fun invoke(): Flow<LibraryContentDataModel> {
        return combine(
            favoriteMoviesRepository.observeFavoriteMovies(),
            savedPeopleRepository.observeSavedPeople()
        ) { movies, people ->
            LibraryContentDataModel(
                movies = movies,
                people = people
            )
        }
    }
}
