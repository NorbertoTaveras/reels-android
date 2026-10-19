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
        val plans = entryPoint.movieNightRepository()
            .getMovieNights()
            .filter { it.status.isVisibleInWidget() }
            .sortedBy(MovieNightPlan::scheduledAt)
            .take(MAX_VISIBLE_MOVIE_NIGHTS)
        val posters = plans.associate { it.id to WidgetPosterLoader.load(context, it.posterPath) }
        val backdrop = WidgetPosterLoader.createBackdrop(posters[plans.firstOrNull()?.id])
        val isOnline = entryPoint.networkMonitor().isOnline()
        provideContent { NextMovieNightContent(context, plans, posters, backdrop, isOnline) }
    }
}

class NextMovieNightWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NextMovieNightWidget()
}

@Composable
private fun NextMovieNightContent(
    context: Context,
    plans: List<MovieNightPlan>,
    posters: Map<String, Bitmap?>,
    backdrop: Bitmap?,
    isOnline: Boolean,
) {
    val size = LocalSize.current
    val isCompact = size.width < PlanWidgetDimensions.MediumSize.width ||
        size.height < PlanWidgetDimensions.MediumSize.height
    val isExpanded = size.width >= PlanWidgetDimensions.ExpandedSize.width
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(PlanWidgetTheme.Background)
            .cornerRadius(PlanWidgetDimensions.CornerSize)
            .clickable(plansAction(context, MOVIE_NIGHTS_TAB))
            .padding(if (isCompact) PlanWidgetDimensions.CompactPadding else PlanWidgetDimensions.ContentPadding),
    ) {
        WidgetHeader(context.getString(R.string.widget_next_movie_night))
        Spacer(GlanceModifier.height(if (isCompact) 3.dp else 6.dp))
        when {
            plans.isEmpty() -> EmptyWidgetMessage(
                context = context,
                message = context.getString(R.string.widget_no_movie_night),
                compact = isCompact,
            )

            isCompact -> MovieNightRow(context, plans.first(), posters[plans.first().id], isOnline)
            else -> MovieNightCollection(
                context = context,
                plans = plans,
                posters = posters,
                backdrop = backdrop,
                isExpanded = isExpanded,
                isOnline = isOnline,
                primaryModifier = GlanceModifier.defaultWeight(),
            )
        }
    }
}

@Composable
private fun MovieNightCollection(
    context: Context,
    plans: List<MovieNightPlan>,
    posters: Map<String, Bitmap?>,
    backdrop: Bitmap?,
    isExpanded: Boolean,
    isOnline: Boolean,
    primaryModifier: GlanceModifier,
) {
    val primary = plans.first()
    if (isExpanded && plans.size > 1) {
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            WidgetBackdrop(
                title = primary.title,
                backdrop = backdrop,
                modifier = GlanceModifier.defaultWeight(),
            ) {
                MovieNightHero(context, primary, isOnline)
            }
            Spacer(GlanceModifier.width(6.dp))
            SecondaryMovieNightRow(context, plans[1], posters[plans[1].id], isOnline)
        }
    } else {
        WidgetBackdrop(
            title = primary.title,
            backdrop = backdrop,
            modifier = primaryModifier,
            fillHeight = true,
        ) {
            MovieNightHero(context, primary, isOnline)
        }
    }
}

@Composable
private fun MovieNightHero(context: Context, plan: MovieNightPlan, isOnline: Boolean) {
    Column {
        Text(
            text = plan.title,
            maxLines = 1,
            style = TextStyle(
                color = PlanWidgetTheme.OnImage,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        Text(
            text = plan.detailsLabel(context, isOnline),
            maxLines = 1,
            style = TextStyle(color = PlanWidgetTheme.OnImageSecondary, fontSize = 12.sp),
        )
        MovieNightJourneyAction(context, plan)
    }
}

@Composable
private fun SecondaryMovieNightRow(
    context: Context,
    plan: MovieNightPlan,
    poster: Bitmap?,
    isOnline: Boolean,
) {
    Row(
        modifier = GlanceModifier
            .width(112.dp)
            .clickable(movieAction(context, plan.movieId)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetMiniPoster(plan.title, poster)
        Spacer(GlanceModifier.width(6.dp))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = plan.title,
                maxLines = 1,
                style = TextStyle(
                    color = PlanWidgetTheme.Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = if (isOnline) plan.toWidgetDateTime() else context.getString(R.string.widget_cached),
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 12.sp),
            )
        }
    }
}

@Composable
private fun MovieNightRow(
    context: Context,
    plan: MovieNightPlan,
    poster: Bitmap?,
    isOnline: Boolean,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .clickable(movieAction(context, plan.movieId)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetPoster(title = plan.title, poster = poster)
        Spacer(GlanceModifier.width(PlanWidgetDimensions.ContentSpacing))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = plan.title,
                maxLines = 2,
                style = TextStyle(
                    color = PlanWidgetTheme.Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = if (isOnline) plan.toWidgetDateTime() else context.getString(R.string.widget_cached),
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 12.sp),
            )
            if (plan.canStartFromWidget()) MovieNightJourneyAction(context, plan)
        }
    }
}

@Composable
private fun MovieNightJourneyAction(context: Context, plan: MovieNightPlan) {
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
                    fontSize = 12.sp,
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
                    .padding(horizontal = 9.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = context.getString(R.string.widget_start_action),
                    style = TextStyle(
                        color = PlanWidgetTheme.OnAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }

        else -> Text(
            text = plan.reminderLabel(context),
            maxLines = 1,
            style = TextStyle(color = PlanWidgetTheme.OnImageSecondary, fontSize = 12.sp),
        )
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

private fun MovieNightPlan.detailsLabel(context: Context, isOnline: Boolean): String =
    if (isOnline) "${toWidgetDateTime()} · ${runtimeLabel(context)}" else context.getString(R.string.widget_cached)

private fun MovieNightPlan.reminderLabel(context: Context): String = reminderMinutesBefore?.let {
    context.getString(R.string.widget_reminder_minutes_before, it)
} ?: context.getString(R.string.widget_reminder_none)

private const val MOVIE_NIGHTS_TAB = "movie_nights"
private const val START_WINDOW_MILLIS = 60 * 60 * 1_000L
private const val MAX_VISIBLE_MOVIE_NIGHTS = 2
