/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.widget

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.data.widget.PlanWidgetEntryPoint
import com.norbertotaveras.reels.data.widget.WidgetPosterLoader
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.model.plans.MovieNightStatus
import dagger.hilt.android.EntryPointAccessors
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NextMovieNightWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Responsive(
        setOf(
            PlanWidgetDimensions.CompactSize,
            PlanWidgetDimensions.MediumSize,
            PlanWidgetDimensions.ExpandedSize,
        ),
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(context, PlanWidgetEntryPoint::class.java)
        val plan = entryPoint.movieNightRepository()
            .getMovieNights()
            .filter { it.status.isVisibleInWidget() }
            .minByOrNull { it.scheduledAt }
        val poster = WidgetPosterLoader.load(context, plan?.posterPath)
        val isOnline = entryPoint.networkMonitor().isOnline()
        provideContent { NextMovieNightContent(context, plan, poster, isOnline) }
    }
}

class NextMovieNightWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NextMovieNightWidget()
}

@Composable
private fun NextMovieNightContent(
    context: Context,
    plan: MovieNightPlan?,
    poster: Bitmap?,
    isOnline: Boolean,
) {
    val size = LocalSize.current
    val isCompact = size.width < PlanWidgetDimensions.MediumSize.width ||
        size.height < PlanWidgetDimensions.MediumSize.height
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(PlanWidgetTheme.Background)
            .cornerRadius(PlanWidgetDimensions.CornerSize)
            .clickable(plansAction(context, MOVIE_NIGHTS_TAB))
            .padding(
                if (isCompact) PlanWidgetDimensions.CompactPadding else PlanWidgetDimensions.ContentPadding,
            ),
    ) {
        WidgetHeader(
            title = context.getString(R.string.widget_next_movie_night),
            isOnline = isOnline,
            compact = isCompact,
        )
        Spacer(GlanceModifier.height(if (isCompact) 3.dp else 6.dp))
        if (plan == null) {
            EmptyWidgetMessage(
                message = context.getString(R.string.widget_no_movie_night),
                actionLabel = context.getString(R.string.widget_open_reels),
            )
        } else {
            MovieNightBody(context, plan, poster, isCompact)
        }
    }
}

@Composable
private fun MovieNightBody(
    context: Context,
    plan: MovieNightPlan,
    poster: Bitmap?,
    compact: Boolean,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .clickable(movieAction(context, plan.movieId)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetPoster(title = plan.title, poster = poster, compact = compact)
        Spacer(GlanceModifier.width(PlanWidgetDimensions.ContentSpacing))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = plan.title,
                maxLines = 1,
                style = TextStyle(
                    color = PlanWidgetTheme.Primary,
                    fontSize = if (compact) 12.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = plan.toWidgetDateTime(),
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 11.sp),
            )
            if (!compact) {
                Text(
                    text = plan.runtimeLabel(context),
                    maxLines = 1,
                    style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 10.sp),
                )
            }
            MovieNightJourneyAction(context, plan, compact)
        }
    }
}

@Composable
private fun MovieNightJourneyAction(context: Context, plan: MovieNightPlan, compact: Boolean) {
    when {
        plan.status == MovieNightStatus.Active || plan.status == MovieNightStatus.Paused -> {
            Text(
                text = if (plan.status == MovieNightStatus.Paused) {
                    context.getString(R.string.movie_night_resume)
                } else {
                    context.getString(R.string.movie_night_in_progress)
                },
                maxLines = 1,
                style = TextStyle(
                    color = PlanWidgetTheme.Accent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }

        plan.canStartFromWidget() -> {
            Box(
                modifier = GlanceModifier
                    .background(PlanWidgetTheme.Accent)
                    .cornerRadius(12.dp)
                    .clickable(
                        actionRunCallback<StartMovieNightAction>(
                            actionParametersOf(StartMovieNightAction.PlanIdKey to plan.id),
                        ),
                    )
                    .padding(horizontal = if (compact) 8.dp else 10.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = context.getString(R.string.movie_night_start_now),
                    style = TextStyle(
                        color = PlanWidgetTheme.OnAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }
    }
}

private fun MovieNightStatus.isVisibleInWidget(): Boolean = when (this) {
    MovieNightStatus.Upcoming,
    MovieNightStatus.StartingSoon,
    MovieNightStatus.Active,
    MovieNightStatus.Paused,
    -> true

    else -> false
}

private fun MovieNightPlan.canStartFromWidget(): Boolean =
    status == MovieNightStatus.StartingSoon ||
        scheduledAt - System.currentTimeMillis() in 0..START_WINDOW_MILLIS

private fun MovieNightPlan.runtimeLabel(context: Context): String =
    runtimeMinutes.takeIf { it in 1..600 }?.let { "$it min" }
        ?: context.getString(R.string.widget_runtime_unavailable)

private fun MovieNightPlan.toWidgetDateTime(): String = Instant.ofEpochMilli(scheduledAt)
    .atZone(ZoneId.systemDefault())
    .format(DateTimeFormatter.ofPattern("EEE, MMM d · h:mm a"))

private const val MOVIE_NIGHTS_TAB = "movie_nights"
private const val START_WINDOW_MILLIS = 60 * 60 * 1_000L
