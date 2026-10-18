/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
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
import androidx.glance.appwidget.action.actionStartActivity
import com.norbertotaveras.reels.MainActivity
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UpcomingReleasesWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Responsive(setOf(CompactSize, MediumSize, ExpandedSize))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            PlanWidgetEntryPoint::class.java,
        )
        val repository = entryPoint.trackedReleaseRepository()
        val releases = repository.getTrackedReleases()
            .sortedBy { it.releaseDate ?: MAX_DATE }
            .take(3)
        val posters = releases.associate { it.movieId to WidgetPosterLoader.load(context, it.posterPath) }
        val isOnline = entryPoint.networkMonitor().isOnline()
        provideContent { UpcomingReleasesContent(context, releases, posters, isOnline) }
    }

    @Composable
    private fun UpcomingReleasesContent(
        context: Context,
        releases: List<TrackedRelease>,
        posters: Map<Int, android.graphics.Bitmap?>,
        isOnline: Boolean,
    ) {
        val size = LocalSize.current
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(PlanWidgetTheme.Background)
                .cornerRadius(22.dp)
                .clickable(plansAction(context, RELEASES_TAB))
                .padding(16.dp),
        ) {
            Text(
                text = context.getString(R.string.widget_upcoming_releases),
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
            if (releases.isEmpty()) {
                EmptyWidgetMessage(context.getString(R.string.widget_no_tracked_releases))
            } else if (size.width < MediumSize.width) {
                ReleaseRow(context, releases.first(), posters[releases.first().movieId], compact = true)
            } else {
                releases.take(if (size.width >= ExpandedSize.width) 3 else 2).forEach { release ->
                    ReleaseRow(context, release, posters[release.movieId], compact = false)
                    Spacer(GlanceModifier.height(8.dp))
                }
            }
        }
    }
}

class UpcomingReleasesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = UpcomingReleasesWidget()
}

@Composable
private fun ReleaseRow(
    context: Context,
    release: TrackedRelease,
    poster: android.graphics.Bitmap?,
    compact: Boolean,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .clickable(movieAction(context, release.movieId)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (poster != null) {
            Image(
                provider = ImageProvider(poster),
                contentDescription = release.title,
                modifier = GlanceModifier
                    .size(if (compact) 44.dp else 52.dp)
                    .cornerRadius(8.dp),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = GlanceModifier
                    .size(if (compact) 44.dp else 52.dp)
                    .background(PlanWidgetTheme.Surface)
                    .cornerRadius(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(ImageProvider(R.drawable.ic_reels_notification), release.title)
            }
        }
        Spacer(GlanceModifier.width(10.dp))
        Column {
            Text(
                text = release.title,
                maxLines = 1,
                style = TextStyle(
                    color = PlanWidgetTheme.Primary,
                    fontSize = if (compact) 14.sp else 15.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = release.releaseDate.toWidgetDate(),
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 12.sp),
            )
        }
    }
}

@Composable
internal fun EmptyWidgetMessage(message: String) {
    Box(GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 14.sp))
    }
}

internal fun plansAction(context: Context, tab: String) = actionStartActivity(
    Intent(Intent.ACTION_VIEW, Uri.parse("reels://plans?tab=$tab"), context, MainActivity::class.java)
)

internal fun movieAction(context: Context, movieId: Int) = actionStartActivity(
    Intent(Intent.ACTION_VIEW, Uri.parse("reels://movie/$movieId"), context, MainActivity::class.java)
)

private fun String?.toWidgetDate(): String {
    if (this == null) return "TBA"
    return runCatching {
        LocalDate.parse(this).format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    }.getOrDefault(this)
}

internal val CompactSize = DpSize(180.dp, 110.dp)
internal val MediumSize = DpSize(280.dp, 150.dp)
internal val ExpandedSize = DpSize(360.dp, 220.dp)
private const val RELEASES_TAB = "releases"
private const val MAX_DATE = "9999-12-31"
