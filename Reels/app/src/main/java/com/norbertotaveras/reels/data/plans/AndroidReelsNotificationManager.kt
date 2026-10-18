/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.plans

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.norbertotaveras.reels.MainActivity
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.model.plans.MovieNightStatus
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease
import com.norbertotaveras.reels.domain.plans.ReelsNotificationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt
import androidx.core.net.toUri

@Singleton
class AndroidReelsNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ReelsNotificationManager {
    private val manager = NotificationManagerCompat.from(context)

    override fun createChannels() {
        val systemManager = context.getSystemService(NotificationManager::class.java)
        systemManager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    RELEASE_CHANNEL_ID,
                    context.getString(R.string.notification_channel_releases),
                    NotificationManager.IMPORTANCE_DEFAULT,
                ),
                NotificationChannel(
                    MOVIE_NIGHT_CHANNEL_ID,
                    context.getString(R.string.notification_channel_movie_nights),
                    NotificationManager.IMPORTANCE_DEFAULT,
                ),
                NotificationChannel(
                    JOURNEY_CHANNEL_ID,
                    context.getString(R.string.notification_channel_movie_night_progress),
                    NotificationManager.IMPORTANCE_LOW,
                ),
            )
        )
    }

    override fun showReleaseReminder(release: TrackedRelease) {
        if (!canPostNotifications())
            return
        val content = release.releaseDate?.let { "Releases $it" } ?: "Release date is now available"
        manager.notify(
            RELEASE_NOTIFICATION_OFFSET + release.movieId,
            baseBuilder(RELEASE_CHANNEL_ID, release.title, content, movieIntent(release.movieId)).build(),
        )
    }

    override fun showMovieNightReminder(plan: MovieNightPlan) {
        if (!canPostNotifications())
            return
        val time = Instant.ofEpochMilli(plan.scheduledAt)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("h:mm a"))
        val startAction = NotificationCompat.Action.Builder(
            0,
            context.getString(R.string.movie_night_start_now),
            actionIntent(plan.id, PlanAlarmContract.ActionMovieNightStart),
        ).build()
        manager.notify(
            movieNightNotificationId(plan.id),
            baseBuilder(
                MOVIE_NIGHT_CHANNEL_ID,
                context.getString(R.string.movie_night_notification_title, plan.title),
                context.getString(R.string.movie_night_notification_time, time),
                plansIntent(MOVIE_NIGHTS_TAB),
            )
                .addAction(startAction)
                .build(),
        )
    }

    override fun showMovieNightProgress(plan: MovieNightPlan) {
        if (plan.status == MovieNightStatus.Active) {
            scheduleProgressTick(plan.id)
        } else {
            cancelProgressTick(plan.id)
        }
        if (!canPostNotifications()) return
        val progress = calculateProgress(plan)
        val isPaused = plan.status == MovieNightStatus.Paused
        val toggleAction = NotificationCompat.Action.Builder(
            0,
            context.getString(if (isPaused) R.string.movie_night_resume else R.string.movie_night_pause),
            actionIntent(
                plan.id,
                if (isPaused) PlanAlarmContract.ActionMovieNightResume else PlanAlarmContract.ActionMovieNightPause,
            ),
        ).build()
        val finishAction = NotificationCompat.Action.Builder(
            0,
            context.getString(R.string.movie_night_finish),
            actionIntent(plan.id, PlanAlarmContract.ActionMovieNightFinish),
        ).build()
        val endAction = NotificationCompat.Action.Builder(
            0,
            context.getString(R.string.movie_night_end),
            actionIntent(plan.id, PlanAlarmContract.ActionMovieNightEnd),
        ).build()
        val builder = baseBuilder(
            JOURNEY_CHANNEL_ID,
            plan.title,
            context.getString(
                if (isPaused) R.string.movie_night_progress_paused else R.string.movie_night_progress_playing,
                progress,
            ),
            plansIntent(MOVIE_NIGHTS_TAB),
        )
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .addAction(toggleAction)
            .addAction(finishAction)
            .addAction(endAction)

        if (Build.VERSION.SDK_INT >= 36) {
            builder.setStyle(NotificationCompat.ProgressStyle().setProgress(progress))
            if (manager.canPostPromotedNotifications())
                builder.setRequestPromotedOngoing(true)
        } else {
            builder.setProgress(100, progress, false)
        }
        manager.notify(movieNightNotificationId(plan.id), builder.build())
    }

    override fun showMovieNightCompleted(plan: MovieNightPlan) {
        cancelProgressTick(plan.id)
        if (!canPostNotifications()) return
        manager.notify(
            movieNightNotificationId(plan.id),
            baseBuilder(
                MOVIE_NIGHT_CHANNEL_ID,
                plan.title,
                context.getString(R.string.movie_night_completed),
                plansIntent(MOVIE_NIGHTS_TAB),
            ).setOngoing(false).build(),
        )
    }

    override fun cancelMovieNightProgress(planId: String) {
        manager.cancel(movieNightNotificationId(planId))
        cancelProgressTick(planId)
    }

    private fun baseBuilder(
        channelId: String,
        title: String,
        content: String,
        contentIntent: PendingIntent,
    ): NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_reels_notification)
        .setContentTitle(title)
        .setContentText(content)
        .setContentIntent(contentIntent)
        .setAutoCancel(true)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)

    override fun canPostNotifications(): Boolean =
        manager.areNotificationsEnabled() && (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED)

    private fun calculateProgress(plan: MovieNightPlan): Int {
        val startedAt = plan.startedAt ?: return 0
        val endPoint = plan.pausedAt ?: System.currentTimeMillis()
        val elapsed = (endPoint - startedAt - plan.accumulatedPausedMillis).coerceAtLeast(0L)
        val duration = plan.runtimeMinutes * 60_000L
        return (elapsed.toDouble() / duration * 100).roundToInt().coerceIn(0, 100)
    }

    private fun movieIntent(movieId: Int): PendingIntent = activityIntent(
        requestCode = MOVIE_REQUEST_OFFSET + movieId,
        uri = "reels://movie/$movieId",
    )

    private fun plansIntent(tab: String): PendingIntent = activityIntent(
        requestCode = if (tab == RELEASES_TAB) RELEASES_REQUEST_CODE else MOVIE_NIGHTS_REQUEST_CODE,
        uri = "reels://plans?tab=$tab",
    )

    private fun activityIntent(requestCode: Int, uri: String): PendingIntent = PendingIntent.getActivity(
        context,
        requestCode,
        Intent(Intent.ACTION_VIEW, uri.toUri(), context, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun actionIntent(planId: String, action: String): PendingIntent = PendingIntent.getBroadcast(
        context,
        action.hashCode() + planId.hashCode(),
        Intent(context, MovieNightActionReceiver::class.java)
            .setAction(action)
            .putExtra(PlanAlarmContract.ExtraPlanId, planId),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun movieNightNotificationId(planId: String): Int =
        MOVIE_NIGHT_NOTIFICATION_OFFSET + planId.hashCode()

    private fun scheduleProgressTick(planId: String) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + PROGRESS_UPDATE_INTERVAL_MILLIS,
            progressPendingIntent(planId),
        )
    }

    private fun cancelProgressTick(planId: String) {
        context.getSystemService(AlarmManager::class.java).cancel(progressPendingIntent(planId))
    }

    private fun progressPendingIntent(planId: String): PendingIntent = PendingIntent.getBroadcast(
        context,
        PROGRESS_REQUEST_OFFSET + planId.hashCode(),
        Intent(context, MovieNightActionReceiver::class.java)
            .setAction(PlanAlarmContract.ActionMovieNightProgress)
            .putExtra(PlanAlarmContract.ExtraPlanId, planId),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private companion object {
        const val RELEASE_CHANNEL_ID = "tracked_releases"
        const val MOVIE_NIGHT_CHANNEL_ID = "movie_nights"
        const val JOURNEY_CHANNEL_ID = "movie_night_progress"
        const val RELEASE_NOTIFICATION_OFFSET = 30_000
        const val MOVIE_NIGHT_NOTIFICATION_OFFSET = 40_000
        const val MOVIE_REQUEST_OFFSET = 50_000
        const val RELEASES_REQUEST_CODE = 60_001
        const val MOVIE_NIGHTS_REQUEST_CODE = 60_002
        const val PROGRESS_REQUEST_OFFSET = 70_000
        const val PROGRESS_UPDATE_INTERVAL_MILLIS = 60_000L
        const val RELEASES_TAB = "releases"
        const val MOVIE_NIGHTS_TAB = "movie_nights"
    }
}
