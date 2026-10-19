/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
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
import com.norbertotaveras.reels.MainActivity
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.data.widget.PlanWidgetEntryPoint
import com.norbertotaveras.reels.data.widget.WidgetPosterLoader
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease
import dagger.hilt.android.EntryPointAccessors

class UpcomingReleasesWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Responsive(
        setOf(
            PlanWidgetDimensions.CompactSize,
            PlanWidgetDimensions.MediumSize,
            PlanWidgetDimensions.ExpandedSize,
        ),
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(context, PlanWidgetEntryPoint::class.java)
        val releases = entryPoint.trackedReleaseRepository()
            .getTrackedReleases()
            .sortedBy { it.releaseDate ?: MAX_DATE }
            .take(MAX_VISIBLE_RELEASES)
        val posters = releases.associate { it.movieId to WidgetPosterLoader.load(context, it.posterPath) }
        val backdrop = WidgetPosterLoader.createBackdrop(posters[releases.firstOrNull()?.movieId])
        val isOnline = entryPoint.networkMonitor().isOnline()
        provideContent { UpcomingReleasesContent(context, releases, posters, backdrop, isOnline) }
    }
}

class UpcomingReleasesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = UpcomingReleasesWidget()
}

@Composable
private fun UpcomingReleasesContent(
    context: Context,
    releases: List<TrackedRelease>,
    posters: Map<Int, Bitmap?>,
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
            .clickable(plansAction(context, RELEASES_TAB))
            .padding(if (isCompact) PlanWidgetDimensions.CompactPadding else PlanWidgetDimensions.ContentPadding),
    ) {
        WidgetHeader(
            context.getString(
                if (isCompact) R.string.widget_upcoming else R.string.widget_upcoming_releases,
            ),
        )
        Spacer(GlanceModifier.height(if (isCompact) 3.dp else 6.dp))
        when {
            releases.isEmpty() -> EmptyWidgetMessage(
                context = context,
                message = context.getString(R.string.widget_no_tracked_releases),
                compact = isCompact,
            )

            isCompact -> ReleaseRow(context, releases.first(), posters[releases.first().movieId], isOnline)
            else -> ReleaseCollection(
                context = context,
                releases = releases,
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
private fun ReleaseCollection(
    context: Context,
    releases: List<TrackedRelease>,
    posters: Map<Int, Bitmap?>,
    backdrop: Bitmap?,
    isExpanded: Boolean,
    isOnline: Boolean,
    primaryModifier: GlanceModifier,
) {
    val primary = releases.first()
    if (isExpanded && releases.size > 1) {
        ExpandedReleaseCollection(context, releases, posters, isOnline)
    } else {
        WidgetBackdrop(
            title = primary.title,
            backdrop = backdrop,
            modifier = if (releases.size == 1) primaryModifier else GlanceModifier.fillMaxWidth(),
            height = if (releases.size == 1) {
                PlanWidgetDimensions.SingleHeroHeight
            } else {
                PlanWidgetDimensions.HeroHeight
            },
            fillHeight = releases.size == 1,
        ) {
            ReleaseHero(context, primary, isOnline)
        }
        releases.getOrNull(1)?.let { release ->
            Spacer(GlanceModifier.height(4.dp))
            SecondaryReleaseRow(context, release, posters[release.movieId], isOnline)
        }
    }
}

@Composable
private fun ExpandedReleaseCollection(
    context: Context,
    releases: List<TrackedRelease>,
    posters: Map<Int, Bitmap?>,
    isOnline: Boolean,
) {
    releases.take(MAX_VISIBLE_RELEASES).forEachIndexed { index, release ->
        ExpandedReleaseRow(context, release, posters[release.movieId], isOnline)
        if (index < releases.lastIndex && index < MAX_VISIBLE_RELEASES - 1) WidgetDivider()
    }
    if (!isOnline) {
        Text(
            text = context.getString(R.string.widget_locally_stored_releases),
            maxLines = 1,
            style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 10.sp),
        )
    }
}

@Composable
private fun ExpandedReleaseRow(
    context: Context,
    release: TrackedRelease,
    poster: Bitmap?,
    isOnline: Boolean,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .clickable(movieAction(context, release.movieId)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetMiniPoster(release.title, poster)
        Spacer(GlanceModifier.width(6.dp))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = release.title,
                maxLines = 1,
                style = TextStyle(
                    color = PlanWidgetTheme.Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = release.dateLabel(context, isOnline),
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 11.sp),
            )
        }
        release.releaseDate?.let { WidgetCountdownBadge(it.toWidgetReleaseCountdown()) }
    }
}

@Composable
private fun ReleaseHero(context: Context, release: TrackedRelease, isOnline: Boolean) {
    Column {
        Text(
            text = release.title,
            maxLines = 1,
            style = TextStyle(
                color = PlanWidgetTheme.OnImage,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        Text(
            text = release.dateLabel(context, isOnline),
            maxLines = 1,
            style = TextStyle(color = PlanWidgetTheme.OnImageSecondary, fontSize = 12.sp),
        )
        Text(
            text = release.reminder.toWidgetLabel(context),
            maxLines = 1,
            style = TextStyle(color = PlanWidgetTheme.OnImageSecondary, fontSize = 12.sp),
        )
    }
}

@Composable
private fun SecondaryReleaseRow(
    context: Context,
    release: TrackedRelease,
    poster: Bitmap?,
    isOnline: Boolean,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .clickable(movieAction(context, release.movieId)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetMiniPoster(release.title, poster)
        Spacer(GlanceModifier.width(6.dp))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = release.title,
                maxLines = 1,
                style = TextStyle(
                    color = PlanWidgetTheme.Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = release.dateLabel(context, isOnline),
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 12.sp),
            )
        }
    }
}

@Composable
private fun ReleaseRow(
    context: Context,
    release: TrackedRelease,
    poster: Bitmap?,
    isOnline: Boolean,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .clickable(movieAction(context, release.movieId)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetPoster(title = release.title, poster = poster)
        Spacer(GlanceModifier.width(PlanWidgetDimensions.ContentSpacing))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = release.title,
                maxLines = 1,
                style = TextStyle(
                    color = PlanWidgetTheme.Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = release.dateLabel(context, isOnline),
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 12.sp),
            )
        }
    }
}

internal fun plansAction(context: Context, tab: String) = actionStartActivity(
    Intent(Intent.ACTION_VIEW, Uri.parse("reels://plans?tab=$tab"), context, MainActivity::class.java),
)

internal fun movieAction(context: Context, movieId: Int) = actionStartActivity(
    Intent(Intent.ACTION_VIEW, Uri.parse("reels://movie/$movieId"), context, MainActivity::class.java),
)

private fun TrackedRelease.dateLabel(context: Context, isOnline: Boolean): String =
    releaseDate.toWidgetReleaseDate(context, isOnline)

private const val RELEASES_TAB = "releases"
private const val MAX_DATE = "9999-12-31"
private const val MAX_VISIBLE_RELEASES = 3
