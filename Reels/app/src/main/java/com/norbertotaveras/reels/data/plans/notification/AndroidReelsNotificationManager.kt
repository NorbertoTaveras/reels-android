/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.plans.notification

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.norbertotaveras.reels.MainActivity
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.data.plans.MovieNightActionReceiver
import com.norbertotaveras.reels.data.plans.scheduling.PlanAlarmContract
import com.norbertotaveras.reels.domain.plans.MovieNightJourney
import com.norbertotaveras.reels.domain.plans.ReelsNotificationManager
import com.norbertotaveras.reels.domain.plans.canStartJourney
import com.norbertotaveras.reels.domain.plans.countdownProgress
import com.norbertotaveras.reels.domain.plans.model.MovieNightPlan
import com.norbertotaveras.reels.domain.plans.model.MovieNightStatus
import com.norbertotaveras.reels.domain.plans.model.TrackedRelease
import com.norbertotaveras.reels.domain.plans.runtimeProgress
import com.norbertotaveras.reels.domain.settings.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class AndroidReelsNotificationManager
@Inject
constructor(
    private val settingsRepository: SettingsRepository,
    @param:ApplicationContext private val context: Context,
) : ReelsNotificationManager {
    private val manager = NotificationManagerCompat.from(context)

    override fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return
        val systemManager = context.getSystemService(NotificationManager::class.java)
        systemManager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    PlanNotificationContract.ReleaseChannelId,
                    context.getString(R.string.notification_channel_releases),
                    NotificationManager.IMPORTANCE_DEFAULT,
                ),
                NotificationChannel(
                    PlanNotificationContract.MovieNightChannelId,
                    context.getString(R.string.notification_channel_movie_nights),
                    NotificationManager.IMPORTANCE_DEFAULT,
                ),
                NotificationChannel(
                    PlanNotificationContract.JourneyChannelId,
                    context.getString(R.string.notification_channel_movie_night_progress),
                    NotificationManager.IMPORTANCE_LOW,
                ),
            )
        )
    }

    override fun showReleaseReminder(release: TrackedRelease) {
        if (!canPostNotifications()) return
        postNotification(
            PlanNotificationContract.ReleaseNotificationOffset + release.movieId,
            baseBuilder(
                    PlanNotificationContract.ReleaseChannelId,
                    release.title,
                    PlanNotificationFormatter.releaseContent(context, release),
                    movieIntent(release.movieId),
                )
                .build(),
        )
    }

    override fun showMovieNightReminder(plan: MovieNightPlan) {
        if (!canPostNotifications()) return
        val builder =
            baseBuilder(
                PlanNotificationContract.MovieNightChannelId,
                context.getString(R.string.movie_night_reminder_title, plan.title),
                context.getString(
                    R.string.movie_night_notification_time,
                    PlanNotificationFormatter.movieNightTime(plan),
                ),
                plansIntent(PlanNotificationContract.MovieNightsTab),
            )
        if (plan.canStartJourney()) builder.addAction(startAction(plan.id))
        postNotification(movieNightReminderNotificationId(plan.id), builder.build())
    }

    override suspend fun showMovieNightCountdown(plan: MovieNightPlan) {
        if (plan.status != MovieNightStatus.StartingSoon) {
            cancelProgressTick(plan.id)
            return
        }
        scheduleProgressTick(plan.id)
        if (!canPostNotifications()) return
        val builder =
            journeyBuilder(
                    plan,
                    PlanNotificationFormatter.countdownContent(context, plan),
                    plan.countdownProgress(),
                )
                .setWhen(plan.scheduledAt)
                .setUsesChronometer(true)
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        setChronometerCountDown(true)
                    } else {
                        setUsesChronometer(false)
                    }
                }
                .addAction(startAction(plan.id))
        postNotification(movieNightJourneyNotificationId(plan.id), builder.build())
    }

    override suspend fun showMovieNightProgress(plan: MovieNightPlan) {
        if (plan.status == MovieNightStatus.Active) {
            scheduleProgressTick(plan.id)
        } else {
            cancelProgressTick(plan.id)
        }
        if (!canPostNotifications()) return
        manager.cancel(movieNightReminderNotificationId(plan.id))
        val progress = plan.runtimeProgress()
        val isPaused = plan.status == MovieNightStatus.Paused
        val toggleAction =
            NotificationCompat.Action.Builder(
                    0,
                    context.getString(
                        if (isPaused) R.string.movie_night_resume else R.string.movie_night_pause
                    ),
                    actionIntent(
                        plan.id,
                        if (isPaused) PlanAlarmContract.ActionMovieNightResume
                        else PlanAlarmContract.ActionMovieNightPause,
                    ),
                )
                .build()
        val finishAction =
            NotificationCompat.Action.Builder(
                    0,
                    context.getString(R.string.movie_night_finish),
                    actionIntent(plan.id, PlanAlarmContract.ActionMovieNightFinish),
                )
                .build()
        val endAction =
            NotificationCompat.Action.Builder(
                    0,
                    context.getString(R.string.movie_night_end),
                    actionIntent(plan.id, PlanAlarmContract.ActionMovieNightEnd),
                )
                .build()
        val builder =
            baseBuilder(
                    PlanNotificationContract.JourneyChannelId,
                    plan.title,
                    context.getString(
                        if (isPaused) R.string.movie_night_progress_paused
                        else R.string.movie_night_progress_playing,
                        progress,
                    ),
                    plansIntent(PlanNotificationContract.MovieNightsTab),
                )
                .addAction(toggleAction)
                .addAction(finishAction)
                .addAction(endAction)
        applyJourneyStyle(builder, progress)
        postNotification(movieNightJourneyNotificationId(plan.id), builder.build())
    }

    override fun showMovieNightCompleted(plan: MovieNightPlan) {
        cancelProgressTick(plan.id)
        manager.cancel(movieNightReminderNotificationId(plan.id))
        if (!canPostNotifications()) return
        postNotification(
            movieNightJourneyNotificationId(plan.id),
            baseBuilder(
                    PlanNotificationContract.MovieNightChannelId,
                    plan.title,
                    context.getString(R.string.movie_night_completed),
                    plansIntent(PlanNotificationContract.MovieNightsTab),
                )
                .setOngoing(false)
                .build(),
        )
    }

    override fun cancelMovieNightProgress(planId: String) {
        manager.cancel(movieNightReminderNotificationId(planId))
        manager.cancel(movieNightJourneyNotificationId(planId))
        cancelProgressTick(planId)
    }

    private fun postNotification(id: Int, notification: Notification) {
        if (!canPostNotifications()) return
        try {
            manager.notify(id, notification)
        } catch (_: SecurityException) {
            // Permission can be revoked between the check and posting the notification.
        }
    }

    private fun baseBuilder(
        channelId: String,
        title: String,
        content: String,
        contentIntent: PendingIntent,
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_reels_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

    override fun canPostNotifications(): Boolean =
        manager.areNotificationsEnabled() &&
            (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED)

    private fun movieIntent(movieId: Int): PendingIntent =
        activityIntent(
            requestCode = PlanNotificationContract.MovieRequestOffset + movieId,
            uri = "reels://movie/$movieId",
        )

    private fun plansIntent(tab: String): PendingIntent =
        activityIntent(
            requestCode =
                if (tab == PlanNotificationContract.ReleasesTab) {
                    PlanNotificationContract.ReleasesRequestCode
                } else {
                    PlanNotificationContract.MovieNightsRequestCode
                },
            uri = "reels://plans?tab=$tab",
        )

    private fun activityIntent(requestCode: Int, uri: String): PendingIntent =
        PendingIntent.getActivity(
            context,
            requestCode,
            Intent(Intent.ACTION_VIEW, uri.toUri(), context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    private fun actionIntent(planId: String, action: String): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            action.hashCode() + planId.hashCode(),
            Intent(context, MovieNightActionReceiver::class.java)
                .setAction(action)
                .putExtra(PlanAlarmContract.ExtraPlanId, planId),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    private fun movieNightReminderNotificationId(planId: String): Int =
        PlanNotificationContract.MovieNightReminderOffset + planId.hashCode()

    private fun movieNightJourneyNotificationId(planId: String): Int =
        PlanNotificationContract.MovieNightJourneyOffset + planId.hashCode()

    private fun scheduleProgressTick(planId: String) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + MovieNightJourney.UPDATE_INTERVAL_MILLIS,
            progressPendingIntent(planId),
        )
    }

    private fun cancelProgressTick(planId: String) {
        context.getSystemService(AlarmManager::class.java).cancel(progressPendingIntent(planId))
    }

    private fun progressPendingIntent(planId: String): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            PlanNotificationContract.ProgressRequestOffset + planId.hashCode(),
            Intent(context, MovieNightActionReceiver::class.java)
                .setAction(PlanAlarmContract.ActionMovieNightProgress)
                .putExtra(PlanAlarmContract.ExtraPlanId, planId),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    override fun supportsLiveUpdates(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA

    override fun canPromoteLiveUpdates(): Boolean =
        supportsLiveUpdates() && manager.canPostPromotedNotifications()

    private suspend fun journeyBuilder(
        plan: MovieNightPlan,
        content: String,
        progress: Int,
    ): NotificationCompat.Builder =
        baseBuilder(
                PlanNotificationContract.JourneyChannelId,
                plan.title,
                content,
                plansIntent(PlanNotificationContract.MovieNightsTab),
            )
            .also { applyJourneyStyle(it, progress) }

    private suspend fun applyJourneyStyle(builder: NotificationCompat.Builder, progress: Int) {
        builder
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            val accent = ContextCompat.getColor(context, R.color.reels_notification_accent)
            builder.setStyle(
                NotificationCompat.ProgressStyle()
                    .addProgressSegment(
                        NotificationCompat.ProgressStyle.Segment(
                                PlanNotificationContract.ProgressMax
                            )
                            .setColor(accent)
                    )
                    .setStyledByProgress(true)
                    .setProgress(progress)
            )
            if (
                manager.canPostPromotedNotifications() &&
                    settingsRepository.observeSettings().first().planPreferences.liveUpdatesEnabled
            ) {
                builder.setRequestPromotedOngoing(true)
            }
        } else {
            builder.setProgress(PlanNotificationContract.ProgressMax, progress, false)
        }
    }

    private fun startAction(planId: String): NotificationCompat.Action =
        NotificationCompat.Action.Builder(
                0,
                context.getString(R.string.movie_night_start_now),
                actionIntent(planId, PlanAlarmContract.ActionMovieNightStart),
            )
            .build()
}
