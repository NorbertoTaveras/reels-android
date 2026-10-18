/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.plans

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.AlarmManagerCompat
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.model.plans.MovieNightStatus
import com.norbertotaveras.reels.domain.model.plans.ReleaseReminder
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease
import com.norbertotaveras.reels.domain.plans.PlanScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidPlanScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : PlanScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun canScheduleExactAlarms(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    override fun scheduleRelease(release: TrackedRelease) {
        cancelRelease(release.movieId)
        val triggerAt = releaseReminderAt(release) ?: return
        if (triggerAt <= System.currentTimeMillis())
            return
        schedule(triggerAt, releasePendingIntent(release.movieId))
    }

    override fun cancelRelease(movieId: Int) {
        alarmManager.cancel(releasePendingIntent(movieId))
    }

    override fun scheduleMovieNight(plan: MovieNightPlan) {
        cancelMovieNight(plan.id)
        if (plan.status != MovieNightStatus.Upcoming && plan.status != MovieNightStatus.StartingSoon)
            return
        if (plan.status == MovieNightStatus.Upcoming) {
            plan.reminderMinutesBefore?.let { reminderMinutes ->
                val triggerAt = plan.scheduledAt - reminderMinutes * MILLIS_PER_MINUTE
                if (triggerAt > System.currentTimeMillis()) {
                    schedule(triggerAt, movieNightPendingIntent(plan.id))
                }
            }
        }
        val missedAt = plan.scheduledAt + MISSED_GRACE_MILLIS
        if (missedAt > System.currentTimeMillis()) {
            schedule(missedAt, movieNightMissedPendingIntent(plan.id))
        }
    }

    override fun cancelMovieNight(id: String) {
        alarmManager.cancel(movieNightPendingIntent(id))
        alarmManager.cancel(movieNightMissedPendingIntent(id))
    }

    private fun schedule(triggerAt: Long, operation: PendingIntent) {
        if (canScheduleExactAlarms()) {
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                operation,
            )
        } else {
            alarmManager.setWindow(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                INEXACT_WINDOW_MILLIS,
                operation,
            )
        }
    }

    private fun releaseReminderAt(release: TrackedRelease): Long? {
        if (!release.notificationsEnabled || release.reminder == ReleaseReminder.None)
            return null

        if (release.reminder == ReleaseReminder.Custom)
            return release.customReminderAt

        val date = release.releaseDate?.let { value ->
            runCatching { LocalDate.parse(value) }.getOrNull()
        } ?: return null

        val daysBefore = (release.reminder.minutesBefore ?: DEFAULT_RELEASE_MINUTES) / MINUTES_PER_DAY

        return date
            .minusDays(daysBefore.toLong())
            .atTime(DEFAULT_RELEASE_REMINDER_TIME)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun releasePendingIntent(movieId: Int): PendingIntent = PendingIntent.getBroadcast(
        context,
        RELEASE_REQUEST_OFFSET + movieId,
        Intent(context, PlanAlarmReceiver::class.java)
            .setAction(PlanAlarmContract.ActionReleaseReminder)
            .putExtra(PlanAlarmContract.ExtraMovieId, movieId),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun movieNightPendingIntent(planId: String): PendingIntent = PendingIntent.getBroadcast(
        context,
        MOVIE_NIGHT_REQUEST_OFFSET + planId.hashCode(),
        Intent(context, PlanAlarmReceiver::class.java)
            .setAction(PlanAlarmContract.ActionMovieNightReminder)
            .putExtra(PlanAlarmContract.ExtraPlanId, planId),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun movieNightMissedPendingIntent(planId: String): PendingIntent = PendingIntent.getBroadcast(
        context,
        MOVIE_NIGHT_MISSED_REQUEST_OFFSET + planId.hashCode(),
        Intent(context, PlanAlarmReceiver::class.java)
            .setAction(PlanAlarmContract.ActionMovieNightMissed)
            .putExtra(PlanAlarmContract.ExtraPlanId, planId),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private companion object {
        const val RELEASE_REQUEST_OFFSET = 10_000
        const val MOVIE_NIGHT_REQUEST_OFFSET = 20_000
        const val MOVIE_NIGHT_MISSED_REQUEST_OFFSET = 25_000
        const val MINUTES_PER_DAY = 1_440
        const val DEFAULT_RELEASE_MINUTES = 1_440
        const val MILLIS_PER_MINUTE = 60_000L
        const val INEXACT_WINDOW_MILLIS = 15 * MILLIS_PER_MINUTE
        const val MISSED_GRACE_MILLIS = 15 * MILLIS_PER_MINUTE
        val DEFAULT_RELEASE_REMINDER_TIME: LocalTime = LocalTime.of(9, 0)
    }
}
