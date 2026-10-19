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
import androidx.glance.Image
import androidx.glance.ImageProvider
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
import com.norbertotaveras.reels.MainActivity
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.data.widget.PlanWidgetEntryPoint
import com.norbertotaveras.reels.data.widget.WidgetPosterLoader
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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
        val isOnline = entryPoint.networkMonitor().isOnline()
        provideContent { UpcomingReleasesContent(context, releases, posters, isOnline) }
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
    isOnline: Boolean,
) {
    val size = LocalSize.current
    val isCompact = size.width < PlanWidgetDimensions.MediumSize.width ||
        size.height < PlanWidgetDimensions.MediumSize.height
    val visibleCount = when {
        isCompact -> 1
        size.width >= PlanWidgetDimensions.ExpandedSize.width -> 3
        else -> 2
    }
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(PlanWidgetTheme.Background)
            .cornerRadius(PlanWidgetDimensions.CornerSize)
            .clickable(plansAction(context, RELEASES_TAB))
            .padding(
                if (isCompact) PlanWidgetDimensions.CompactPadding else PlanWidgetDimensions.ContentPadding,
            ),
    ) {
        WidgetHeader(
            title = context.getString(R.string.widget_upcoming_releases),
            isOnline = isOnline,
            compact = isCompact,
        )
        Spacer(GlanceModifier.height(if (isCompact) 3.dp else 6.dp))
        if (releases.isEmpty()) {
            EmptyWidgetMessage(
                message = context.getString(R.string.widget_no_tracked_releases),
                actionLabel = context.getString(R.string.widget_open_reels),
            )
        } else {
            releases.take(visibleCount).forEachIndexed { index, release ->
                ReleaseRow(
                    context = context,
                    release = release,
                    poster = posters[release.movieId],
                    compact = isCompact || visibleCount == 3,
                )
                if (index < visibleCount - 1) Spacer(GlanceModifier.height(PlanWidgetDimensions.RowSpacing))
            }
        }
    }
}

@Composable
internal fun WidgetHeader(title: String, isOnline: Boolean, compact: Boolean) {
    Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            maxLines = 1,
            style = TextStyle(
                color = PlanWidgetTheme.Primary,
                fontSize = if (compact) 11.sp else 12.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = GlanceModifier.defaultWeight(),
        )
        if (!isOnline) {
            Text(
                text = "Cached",
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 10.sp),
            )
        }
    }
}

@Composable
private fun ReleaseRow(
    context: Context,
    release: TrackedRelease,
    poster: Bitmap?,
    compact: Boolean,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .clickable(movieAction(context, release.movieId)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetPoster(title = release.title, poster = poster, compact = compact)
        Spacer(GlanceModifier.width(PlanWidgetDimensions.ContentSpacing))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = release.title,
                maxLines = 1,
                style = TextStyle(
                    color = PlanWidgetTheme.Primary,
                    fontSize = if (compact) 12.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = release.releaseDate.toRelativeReleaseDate(),
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 11.sp),
            )
        }
    }
}

@Composable
internal fun WidgetPoster(title: String, poster: Bitmap?, compact: Boolean) {
    val width = if (compact) PlanWidgetDimensions.CompactPosterWidth else PlanWidgetDimensions.PosterWidth
    val height = if (compact) PlanWidgetDimensions.CompactPosterHeight else PlanWidgetDimensions.PosterHeight
    if (poster != null) {
        Image(
            provider = ImageProvider(poster),
            contentDescription = title,
            modifier = GlanceModifier
                .width(width)
                .height(height)
                .cornerRadius(PlanWidgetDimensions.PosterCornerSize),
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            modifier = GlanceModifier
                .width(width)
                .height(height)
                .background(PlanWidgetTheme.Surface)
                .cornerRadius(PlanWidgetDimensions.PosterCornerSize),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_reels_notification),
                contentDescription = title,
                modifier = GlanceModifier.size(18.dp),
            )
        }
    }
}

@Composable
internal fun EmptyWidgetMessage(message: String, actionLabel: String) {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_reels_notification),
            contentDescription = null,
            modifier = GlanceModifier.size(18.dp),
        )
        Text(
            text = message,
            maxLines = 1,
            style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 10.sp),
        )
        Box(
            modifier = GlanceModifier
                .background(PlanWidgetTheme.Surface)
                .cornerRadius(12.dp)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = actionLabel,
                style = TextStyle(
                    color = PlanWidgetTheme.Primary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                ),
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

private fun String?.toRelativeReleaseDate(): String {
    if (this == null) return "TBA"
    return runCatching {
        val days = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(this))
        when (days) {
            0L -> "Today"
            1L -> "Tomorrow"
            in 2..30 -> "In $days days"
            else -> LocalDate.parse(this).toString()
        }
    }.getOrDefault(this)
}

private const val RELEASES_TAB = "releases"
private const val MAX_DATE = "9999-12-31"
private const val MAX_VISIBLE_RELEASES = 3
