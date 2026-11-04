/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.plans.mapper

import com.norbertotaveras.reels.data.local.plans.entity.MovieNightEntity
import com.norbertotaveras.reels.data.local.plans.entity.TrackedReleaseEntity
import com.norbertotaveras.reels.domain.movie.extensions.toConfirmedReleaseDate
import com.norbertotaveras.reels.domain.plans.model.MovieNightPlan
import com.norbertotaveras.reels.domain.plans.model.MovieNightStatus
import com.norbertotaveras.reels.domain.plans.model.ReleaseReminder
import com.norbertotaveras.reels.domain.plans.model.TrackedRelease

fun TrackedReleaseEntity.toDataModel(): TrackedRelease = TrackedRelease(
    movieId = movieId,
    title = title,
    posterPath = posterPath,
    releaseDate = releaseDate.toConfirmedReleaseDate(),
    previousReleaseDate = previousReleaseDate.toConfirmedReleaseDate(),
    reminder = runCatching { ReleaseReminder.valueOf(reminder) }
        .getOrDefault(ReleaseReminder.AppDefault),
    customReminderAt = customReminderAt,
    notificationsEnabled = notificationsEnabled,
    trackedAt = trackedAt,
    updatedAt = updatedAt
)

fun TrackedRelease.toEntity(): TrackedReleaseEntity = TrackedReleaseEntity(
    movieId = movieId,
    title = title,
    posterPath = posterPath,
    releaseDate = releaseDate,
    previousReleaseDate = previousReleaseDate,
    reminder = reminder.name,
    customReminderAt = customReminderAt,
    notificationsEnabled = notificationsEnabled,
    trackedAt = trackedAt,
    updatedAt = updatedAt
)

fun MovieNightEntity.toDataModel(): MovieNightPlan = MovieNightPlan(
    id = id,
    movieId = movieId,
    title = title,
    posterPath = posterPath,
    scheduledAt = scheduledAt,
    runtimeMinutes = runtimeMinutes,
    reminderMinutesBefore = reminderMinutesBefore,
    venue = venue,
    attendeeCount = attendeeCount,
    note = note,
    status = runCatching { MovieNightStatus.valueOf(status) }
        .getOrDefault(MovieNightStatus.Upcoming),
    startedAt = startedAt,
    pausedAt = pausedAt,
    accumulatedPausedMillis = accumulatedPausedMillis,
    completedAt = completedAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun MovieNightPlan.toEntity(): MovieNightEntity = MovieNightEntity(
    id = id,
    movieId = movieId,
    title = title,
    posterPath = posterPath,
    scheduledAt = scheduledAt,
    runtimeMinutes = runtimeMinutes,
    reminderMinutesBefore = reminderMinutesBefore,
    venue = venue,
    attendeeCount = attendeeCount,
    note = note,
    status = status.name,
    startedAt = startedAt,
    pausedAt = pausedAt,
    accumulatedPausedMillis = accumulatedPausedMillis,
    completedAt = completedAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)
