/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.model.plans.MovieNightStatus
import dagger.hilt.android.EntryPointAccessors
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NextMovieNightWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Responsive(setOf(CompactSize, MediumSize, ExpandedSize))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            PlanWidgetEntryPoint::class.java,
        )
        val repository = entryPoint.movieNightRepository()
        val plan = repository.getMovieNights()
            .filter {
                it.status == MovieNightStatus.Upcoming ||
                    it.status == MovieNightStatus.StartingSoon ||
                    it.status == MovieNightStatus.Active ||
                    it.status == MovieNightStatus.Paused
            }
            .minByOrNull { it.scheduledAt }
        val poster = WidgetPosterLoader.load(context, plan?.posterPath)
        val isOnline = entryPoint.networkMonitor().isOnline()
        provideContent { NextMovieNightContent(context, plan, poster, isOnline) }
    }

    @Composable
    private fun NextMovieNightContent(
        context: Context,
        plan: MovieNightPlan?,
        poster: android.graphics.Bitmap?,
        isOnline: Boolean,
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(PlanWidgetTheme.Background)
                .cornerRadius(22.dp)
                .clickable(plansAction(context, MOVIE_NIGHTS_TAB))
                .padding(16.dp),
        ) {
            Text(
                context.getString(R.string.widget_next_movie_night),
                style = TextStyle(
                    color = PlanWidgetTheme.Primary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            if (!isOnline) {
                Text(
                    context.getString(R.string.widget_offline),
                    style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 11.sp),
                )
            }
            Spacer(GlanceModifier.height(10.dp))
            if (plan == null) {
                EmptyWidgetMessage(context.getString(R.string.widget_no_movie_night))
            } else {
                MovieNightBody(context, plan, poster)
            }
        }
    }
}

class NextMovieNightWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NextMovieNightWidget()
}

@Composable
private fun MovieNightBody(
    context: Context,
    plan: MovieNightPlan,
    poster: android.graphics.Bitmap?,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .clickable(movieAction(context, plan.movieId)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (poster != null && LocalSize.current.width >= MediumSize.width) {
            Image(
                ImageProvider(poster),
                plan.title,
                modifier = GlanceModifier.size(70.dp).cornerRadius(10.dp),
                contentScale = ContentScale.Crop,
            )
            Spacer(GlanceModifier.width(12.dp))
        }
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                plan.title,
                maxLines = 2,
                style = TextStyle(
                    color = PlanWidgetTheme.Primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                plan.toWidgetDateTime(),
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 12.sp),
            )
            Text(
                plan.runtimeMinutes.takeIf { it in 1..600 }?.let { "$it min" }
                    ?: context.getString(R.string.widget_runtime_unavailable),
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 11.sp),
            )
            if (plan.status == MovieNightStatus.Active || plan.status == MovieNightStatus.Paused) {
                Text(
                    if (plan.status == MovieNightStatus.Paused) context.getString(R.string.movie_night_resume) else context.getString(R.string.movie_night_in_progress),
                    style = TextStyle(color = PlanWidgetTheme.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold),
                )
            } else if (
                plan.status == MovieNightStatus.StartingSoon ||
                plan.scheduledAt - System.currentTimeMillis() <= START_WINDOW_MILLIS
            ) {
                Box(
                    modifier = GlanceModifier
                        .background(PlanWidgetTheme.Accent)
                        .cornerRadius(16.dp)
                        .clickable(
                            actionRunCallback<StartMovieNightAction>(
                                actionParametersOf(StartMovieNightAction.PlanIdKey to plan.id)
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        context.getString(R.string.movie_night_start_now),
                        style = TextStyle(color = PlanWidgetTheme.Background, fontSize = 12.sp, fontWeight = FontWeight.Bold),
                    )
                }
            }
        }
    }
}

private fun MovieNightPlan.toWidgetDateTime(): String = Instant.ofEpochMilli(scheduledAt)
    .atZone(ZoneId.systemDefault())
    .format(DateTimeFormatter.ofPattern("EEE, MMM d • h:mm a"))

private const val MOVIE_NIGHTS_TAB = "movie_nights"
private const val START_WINDOW_MILLIS = 60 * 60 * 1_000L
