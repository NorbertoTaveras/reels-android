/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.norbertotaveras.reels.data.local.entity.SavedPersonEntity
import com.norbertotaveras.reels.data.local.entity.SavedPersonKnownForEntity
import com.norbertotaveras.reels.data.local.entity.SavedPersonWithKnownFor
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPersonDao {
    @Transaction
    @Query("SELECT * FROM saved_people ORDER BY savedAt DESC")
    fun observeSavedPeople(): Flow<List<SavedPersonWithKnownFor>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_people WHERE id = :personId)")
    fun observeIsPersonSaved(personId: Int): Flow<Boolean>

    @Transaction
    @Query("SELECT * FROM saved_people WHERE id = :personId LIMIT 1")
    suspend fun getSavedPerson(personId: Int): SavedPersonWithKnownFor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSavedPerson(person: SavedPersonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertKnownForMovies(movies: List<SavedPersonKnownForEntity>)

    @Query("DELETE FROM saved_person_known_for WHERE personId = :personId")
    suspend fun deleteKnownForMovies(personId: Int)

    @Query("DELETE FROM saved_people WHERE id = :personId")
    suspend fun deleteSavedPerson(personId: Int)

    @Transaction
    suspend fun upsertSavedPersonWithKnownFor(
        person: SavedPersonEntity,
        knownFor: List<SavedPersonKnownForEntity>
    ) {
        upsertSavedPerson(person)
        deleteKnownForMovies(personId = person.id)
        if (knownFor.isNotEmpty()) {
            upsertKnownForMovies(knownFor)
        }
    }

    @Transaction
    suspend fun deleteSavedPersonWithKnownFor(personId: Int) {
        deleteKnownForMovies(personId = personId)
        deleteSavedPerson(personId = personId)
    }
}
