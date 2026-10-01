/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.favorites

import com.norbertotaveras.reels.domain.model.favorite.LibraryContentDataModel
import com.norbertotaveras.reels.domain.repository.favorites.FavoriteMoviesRepository
import com.norbertotaveras.reels.domain.repository.favorites.SavedPeopleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

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
