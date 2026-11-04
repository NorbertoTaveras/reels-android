/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.norbertotaveras.reels.data.local.account.entity.CachedMovieAccountStateEntity
import com.norbertotaveras.reels.data.local.cache.entity.CachedContentStatusEntity
import com.norbertotaveras.reels.data.local.movie.entity.CachedGenreEntity
import com.norbertotaveras.reels.data.local.movie.entity.CachedMovieCastEntity
import com.norbertotaveras.reels.data.local.movie.entity.CachedMovieDetailsEntity
import com.norbertotaveras.reels.data.local.movie.entity.CachedMovieVideoEntity
import com.norbertotaveras.reels.data.local.person.entity.CachedPersonDetailsEntity
import com.norbertotaveras.reels.data.local.person.entity.CachedPersonMovieCreditEntity

@Dao
interface CachedContentDao {
    @Query(
        "SELECT EXISTS(SELECT 1 FROM cached_content_status WHERE cacheKey = :cacheKey AND contentType = :contentType)"
    )
    suspend fun hasContent(cacheKey: String, contentType: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertContentStatus(status: CachedContentStatusEntity)

    @Query(
        "SELECT * FROM cached_movie_account_states WHERE accountId = :accountId AND movieId = :movieId LIMIT 1"
    )
    suspend fun getMovieAccountState(
        accountId: Int,
        movieId: Int,
    ): CachedMovieAccountStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovieAccountState(state: CachedMovieAccountStateEntity)

    @Query("SELECT * FROM cached_movie_details WHERE cacheKey = :cacheKey LIMIT 1")
    suspend fun getMovieDetails(cacheKey: String): CachedMovieDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovieDetails(details: CachedMovieDetailsEntity)

    @Query("SELECT * FROM cached_movie_cast WHERE cacheKey = :cacheKey ORDER BY position")
    suspend fun getMovieCast(cacheKey: String): List<CachedMovieCastEntity>

    @Query("DELETE FROM cached_movie_cast WHERE cacheKey = :cacheKey")
    suspend fun clearMovieCast(cacheKey: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovieCast(cast: List<CachedMovieCastEntity>)

    @Query("SELECT * FROM cached_movie_videos WHERE cacheKey = :cacheKey ORDER BY position")
    suspend fun getMovieVideos(cacheKey: String): List<CachedMovieVideoEntity>

    @Query("DELETE FROM cached_movie_videos WHERE cacheKey = :cacheKey")
    suspend fun clearMovieVideos(cacheKey: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovieVideos(videos: List<CachedMovieVideoEntity>)

    @Query("SELECT * FROM cached_person_details WHERE cacheKey = :cacheKey LIMIT 1")
    suspend fun getPersonDetails(cacheKey: String): CachedPersonDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPersonDetails(details: CachedPersonDetailsEntity)

    @Query(
        "SELECT * FROM cached_person_movie_credits WHERE cacheKey = :cacheKey ORDER BY position"
    )
    suspend fun getPersonMovieCredits(cacheKey: String): List<CachedPersonMovieCreditEntity>

    @Query("DELETE FROM cached_person_movie_credits WHERE cacheKey = :cacheKey")
    suspend fun clearPersonMovieCredits(cacheKey: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPersonMovieCredits(credits: List<CachedPersonMovieCreditEntity>)

    @Query("SELECT * FROM cached_genres WHERE language = :language ORDER BY name")
    suspend fun getGenres(language: String): List<CachedGenreEntity>

    @Query("DELETE FROM cached_genres WHERE language = :language")
    suspend fun clearGenres(language: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGenres(genres: List<CachedGenreEntity>)
}
