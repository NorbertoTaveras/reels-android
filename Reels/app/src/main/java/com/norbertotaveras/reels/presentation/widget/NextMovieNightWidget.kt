/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.widget

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
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
import androidx.glance.currentState
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
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.model.plans.MovieNightStatus
import com.norbertotaveras.reels.presentation.ui.theme.PlanWidgetDimensions
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.map

class NextMovieNightWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(context, PlanWidgetEntryPoint::class.java)
        val repository = entryPoint.movieNightRepository()
        val initialPlans = repository.getMovieNights().visibleWidgetPlans()
        provideContent {
            val plans by remember(repository) {
                repository.observeMovieNights().map { it.visibleWidgetPlans() }
            }.collectAsState(initialPlans)
            val isOnline by entryPoint.networkMonitor().connectivity
                .collectAsState(entryPoint.networkMonitor().isOnline())
            val artwork = rememberWidgetArtwork(context, plans.associate { it.id to it.posterPath })
            key(currentState<Preferences>()) {
                NextMovieNightContent(context, plans, artwork.posters, artwork.backdrop, isOnline)
            }
        }
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
    val isExpanded = size.width >= PlanWidgetDimensions.ExpandedLayoutMinWidth
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(PlanWidgetTheme.Background)
            .cornerRadius(PlanWidgetDimensions.CornerSize)
            .clickable(plansAction(context, MOVIE_NIGHTS_TAB)),
    ) {
        WidgetHeader(
            context.getString(
                if (isCompact) R.string.widget_movie_night else R.string.widget_next_movie_night,
            ),
        )
        when {
            plans.isEmpty() -> EmptyWidgetMessage(
                context = context,
                message = context.getString(R.string.widget_no_movie_night),
                compact = isCompact,
            )

            isCompact -> MovieNightRow(
                context, plans.first(), posters[plans.first().id], isOnline,
                GlanceModifier.defaultWeight(),
            )
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
    if (isExpanded) {
        ExpandedMovieNightCollection(context, plans, posters, isOnline, primaryModifier)
    } else {
        Column(modifier = primaryModifier.fillMaxWidth()) {
            WidgetBackdrop(
                title = primary.title,
                backdrop = backdrop,
                modifier = GlanceModifier.fillMaxWidth().clickable(movieAction(context, primary.movieId)),
                height = minOf(
                    PlanWidgetDimensions.HeroHeight,
                    (LocalSize.current.height - PlanWidgetDimensions.HeaderSpace -
                        if (primary.canStartFromWidget()) {
                            PlanWidgetDimensions.MediumNightActionDetailsHeight
                        } else {
                            PlanWidgetDimensions.MediumNightDetailsHeight
                        }).coerceAtLeast(0.dp),
                ),
            )
            Column(
                modifier = GlanceModifier.defaultWeight().fillMaxWidth()
                    .clickable(movieAction(context, primary.movieId))
                    .padding(
                        horizontal = PlanWidgetDimensions.ContentPadding,
                        vertical = PlanWidgetDimensions.ReleaseDetailsPadding,
                    ),
            ) {
                MovieNightHero(context, primary, isOnline)
            }
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
                color = PlanWidgetTheme.Primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        Text(
            text = if (plan.canStartFromWidget()) {
                plan.toWidgetCountdown(context)
            } else {
                plan.widgetDateLabel(context, isOnline)
            },
            maxLines = 1,
            style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 12.sp),
        )
        Text(
            text = if (plan.hasWidgetJourney) plan.runtimeRemainingLabel(context) else plan.runtimeLabel(context),
            maxLines = 1,
            style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 12.sp),
        )
        MovieNightJourneyAction(context, plan, fillWidth = plan.canStartFromWidget())
    }
}

@Composable
private fun ExpandedMovieNightCollection(
    context: Context,
    plans: List<MovieNightPlan>,
    posters: Map<String, Bitmap?>,
    isOnline: Boolean,
    modifier: GlanceModifier,
) {
    val availableHeight = (
        LocalSize.current.height - PlanWidgetDimensions.HeaderSpace -
            PlanWidgetDimensions.DividerHeight * (plans.size - 1)
        ).coerceAtLeast(0.dp)
    Column(modifier = modifier.fillMaxWidth()) {
        plans.take(MAX_VISIBLE_MOVIE_NIGHTS).forEachIndexed { index, plan ->
            val heightFraction = when {
                plans.size == 1 -> 1f
                index == 0 -> 2f / 3f
                else -> 1f / 3f
            }
            ExpandedMovieNightRow(
                context = context,
                plan = plan,
                poster = posters[plan.id],
                isOnline = isOnline,
                primary = index == 0,
                rowHeight = availableHeight * heightFraction,
            )
            if (index == 0 && plans.size > 1) WidgetDivider()
        }
    }
}

@Composable
private fun ExpandedMovieNightRow(
    context: Context,
    plan: MovieNightPlan,
    poster: Bitmap?,
    isOnline: Boolean,
    primary: Boolean,
    rowHeight: Dp,
) {
    val targetPosterWidth = if (primary) {
        PlanWidgetDimensions.PrimaryNightPosterWidth
    } else {
        PlanWidgetDimensions.SecondaryNightPosterWidth
    }
    val targetPosterHeight = if (primary) {
        PlanWidgetDimensions.PrimaryNightPosterHeight
    } else {
        PlanWidgetDimensions.SecondaryNightPosterHeight
    }
    val posterHeight = minOf(targetPosterHeight, rowHeight)
    Row(
        modifier = GlanceModifier.height(rowHeight)
            .fillMaxWidth()
            .clickable(movieAction(context, plan.movieId))
            .padding(horizontal = PlanWidgetDimensions.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetPoster(
            title = plan.title,
            poster = poster,
            width = targetPosterWidth * (posterHeight / targetPosterHeight),
            height = posterHeight,
        )
        Spacer(GlanceModifier.width(PlanWidgetDimensions.NightRowSpacing))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = plan.title,
                maxLines = 1,
                style = TextStyle(
                    color = if (primary) PlanWidgetTheme.Primary else PlanWidgetTheme.Secondary,
                    fontSize = if (primary) 13.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = plan.widgetDateLabel(context, isOnline),
                maxLines = 1,
                style = TextStyle(
                    color = if (primary) {
                        PlanWidgetTheme.Accent
                    } else {
                        PlanWidgetTheme.Secondary
                    },
                    fontSize = 12.sp,
                ),
            )
            if (primary) {
                Text(
                    text = if (plan.hasWidgetJourney) {
                        plan.runtimeRemainingLabel(context)
                    } else {
                        "${plan.toWidgetCountdown(context)} · ${plan.runtimeLabel(context)}"
                    },
                    maxLines = 1,
                    style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 12.sp),
                )
            }
        }
        Spacer(GlanceModifier.width(PlanWidgetDimensions.ContentSpacing))
        if (primary && (plan.canStartFromWidget() || plan.hasWidgetJourney)) {
            MovieNightJourneyAction(context, plan)
        } else if (!primary) {
            WidgetCountdown(plan.toWidgetCountdown(context, short = true))
        }
    }
}

@Composable
private fun MovieNightRow(
    context: Context,
    plan: MovieNightPlan,
    poster: Bitmap?,
    isOnline: Boolean,
    modifier: GlanceModifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(movieAction(context, plan.movieId))
            .padding(
                start = PlanWidgetDimensions.ContentPadding,
                top = PlanWidgetDimensions.CompactTopPadding,
                end = PlanWidgetDimensions.ContentPadding,
                bottom = PlanWidgetDimensions.CompactBottomPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetPoster(title = plan.title, poster = poster)
        Spacer(GlanceModifier.width(PlanWidgetDimensions.ContentSpacing))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = plan.title,
                maxLines = if (plan.hasWidgetJourney ||
                    LocalSize.current.height < PlanWidgetDimensions.CompactTwoLineMinHeight
                ) 1 else 2,
                style = TextStyle(
                    color = PlanWidgetTheme.Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(GlanceModifier.height(PlanWidgetDimensions.CompactTitleSpacing))
            Text(
                text = if (plan.hasWidgetJourney) {
                    plan.runtimeRemainingLabel(context)
                } else {
                    plan.widgetDateLabel(context, isOnline)
                },
                maxLines = 1,
                style = TextStyle(
                    color = PlanWidgetTheme.Accent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
            if (plan.hasWidgetJourney &&
                LocalSize.current.height >= PlanWidgetDimensions.CompactTwoLineMinHeight
            ) {
                MovieNightJourneyAction(context, plan)
            }
        }
    }
}

@Composable
private fun MovieNightJourneyAction(
    context: Context,
    plan: MovieNightPlan,
    fillWidth: Boolean = false,
) {
    when {
        plan.status == MovieNightStatus.Active || plan.status == MovieNightStatus.Paused -> {
            Text(
                text = if (plan.status == MovieNightStatus.Paused) {
                    context.getString(R.string.widget_movie_night_paused)
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
                modifier = (if (fillWidth) GlanceModifier.fillMaxWidth() else GlanceModifier)
                    .background(PlanWidgetTheme.Accent)
                    .cornerRadius(PlanWidgetDimensions.StartCornerSize)
                    .clickable(
                        actionRunCallback<StartMovieNightAction>(
                            actionParametersOf(StartMovieNightAction.PlanIdKey to plan.id),
                        ),
                    )
                    .padding(
                        horizontal = PlanWidgetDimensions.StartHorizontalPadding,
                        vertical = PlanWidgetDimensions.StartVerticalPadding,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WidgetIcon(Icons.Filled.PlayArrow, PlanWidgetDimensions.StartIconSize, PlanWidgetTheme.OnAccent)
                    Spacer(GlanceModifier.width(PlanWidgetDimensions.HeaderItemSpacing))
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
        }

        else -> Text(
            text = plan.reminderLabel(context),
            maxLines = 1,
            style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 12.sp),
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

private fun MovieNightPlan.widgetDateLabel(context: Context, isOnline: Boolean): String =
    if (isOnline) {
        toRelativeWidgetDateTime(context)
    } else {
        context.getString(R.string.widget_cached_date, toRelativeWidgetDateTime(context))
    }

private fun MovieNightPlan.reminderLabel(context: Context): String = reminderMinutesBefore?.let {
    context.getString(R.string.widget_reminder_minutes_before, it)
} ?: context.getString(R.string.widget_reminder_none)

private const val MOVIE_NIGHTS_TAB = "movie_nights"
private const val MAX_VISIBLE_MOVIE_NIGHTS = 2

private fun List<MovieNightPlan>.visibleWidgetPlans(): List<MovieNightPlan> =
    filter { it.status.isVisibleInWidget() }
        .sortedWith(compareBy<MovieNightPlan> { !it.hasWidgetJourney }.thenBy(MovieNightPlan::scheduledAt))
        .take(MAX_VISIBLE_MOVIE_NIGHTS)
