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
import androidx.compose.material.icons.outlined.WifiOff
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
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
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
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.data.widget.PlanWidgetEntryPoint
import com.norbertotaveras.reels.domain.plans.model.TrackedRelease
import com.norbertotaveras.reels.presentation.ui.theme.PlanWidgetDimensions
import com.norbertotaveras.reels.presentation.widget.releases.WidgetReleaseMetadata
import com.norbertotaveras.reels.presentation.widget.shared.EmptyWidgetMessage
import com.norbertotaveras.reels.presentation.widget.shared.PlanWidgetTheme
import com.norbertotaveras.reels.presentation.widget.shared.WidgetBackdrop
import com.norbertotaveras.reels.presentation.widget.shared.WidgetDivider
import com.norbertotaveras.reels.presentation.widget.shared.WidgetHeader
import com.norbertotaveras.reels.presentation.widget.shared.WidgetIcon
import com.norbertotaveras.reels.presentation.widget.shared.WidgetPoster
import com.norbertotaveras.reels.presentation.widget.shared.isImminentWidgetRelease
import com.norbertotaveras.reels.presentation.widget.shared.rememberWidgetArtwork
import com.norbertotaveras.reels.presentation.widget.shared.toCompactWidgetReleaseDate
import com.norbertotaveras.reels.presentation.widget.shared.toWidgetLabel
import com.norbertotaveras.reels.presentation.widget.shared.toWidgetReleaseCountdown
import com.norbertotaveras.reels.presentation.widget.shared.toWidgetReleaseDate
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.map

class UpcomingReleasesWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(context, PlanWidgetEntryPoint::class.java)
        val repository = entryPoint.trackedReleaseRepository()
        val initialReleases = repository.getTrackedReleases().visibleWidgetReleases()
        provideContent {
            val releases by remember(repository) {
                repository.observeTrackedReleases().map { it.visibleWidgetReleases() }
            }.collectAsState(initialReleases)
            val isOnline by entryPoint.networkMonitor().connectivity
                .collectAsState(entryPoint.networkMonitor().isOnline())
            val artwork = rememberWidgetArtwork(context, releases.associate { it.movieId to it.posterPath })
            key(currentState<Preferences>()) {
                UpcomingReleasesContent(context, releases, artwork.posters, artwork.backdrop, isOnline)
            }
        }
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
    val isExpanded = size.width >= PlanWidgetDimensions.ExpandedLayoutMinWidth
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(PlanWidgetTheme.Background)
            .cornerRadius(PlanWidgetDimensions.CornerSize)
            .clickable(plansAction(context, RELEASES_TAB)),
    ) {
        WidgetHeader(
            context.getString(
                if (isCompact) R.string.widget_upcoming else R.string.widget_upcoming_releases,
            ),
        )
        when {
            releases.isEmpty() -> EmptyWidgetMessage(
                context = context,
                message = context.getString(R.string.widget_no_tracked_releases),
                compact = isCompact,
            )

            isCompact -> ReleaseRow(
                context, releases.first(), posters[releases.first().movieId], isOnline,
                GlanceModifier.defaultWeight(),
            )
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
    if (isExpanded) {
        ExpandedReleaseCollection(context, releases, posters, isOnline, primaryModifier)
    } else {
        Column(modifier = primaryModifier.fillMaxWidth()) {
            WidgetBackdrop(
                title = primary.title,
                backdrop = backdrop,
                modifier = GlanceModifier.fillMaxWidth()
                    .clickable(movieAction(context, primary.movieId)),
                height = minOf(
                    PlanWidgetDimensions.HeroHeight,
                    (LocalSize.current.height - PlanWidgetDimensions.HeaderSpace -
                        PlanWidgetDimensions.MediumReleaseDetailsHeight -
                        if (releases.size > 1) {
                            PlanWidgetDimensions.SecondaryReleaseRowHeight + PlanWidgetDimensions.DividerHeight
                        } else {
                            0.dp
                        }).coerceAtLeast(0.dp),
                ),
            )
            Column(
                modifier = GlanceModifier.defaultWeight().fillMaxWidth()
                    .clickable(movieAction(context, primary.movieId))
                    .padding(
                        start = PlanWidgetDimensions.ContentPadding,
                        top = PlanWidgetDimensions.ReleaseDetailsPadding,
                        end = PlanWidgetDimensions.ContentPadding,
                        bottom = PlanWidgetDimensions.MediumReleaseDetailsBottomPadding,
                    ),
            ) {
                ReleaseHero(context, primary, isOnline)
            }
            releases.getOrNull(1)?.let { release ->
                WidgetDivider()
                SecondaryReleaseRow(context, release, posters[release.movieId])
            }
        }
    }
}

@Composable
private fun ExpandedReleaseCollection(
    context: Context,
    releases: List<TrackedRelease>,
    posters: Map<Int, Bitmap?>,
    isOnline: Boolean,
    modifier: GlanceModifier,
) {
    val footerHeight = if (isOnline) 0.dp else PlanWidgetDimensions.CachedFooterHeight
    val rowHeight = (
        LocalSize.current.height - PlanWidgetDimensions.HeaderSpace - footerHeight -
            PlanWidgetDimensions.DividerHeight * (releases.size - 1)
        ).coerceAtLeast(0.dp) / releases.size
    Column(modifier = modifier.fillMaxWidth()) {
        releases.forEachIndexed { index, release ->
            ExpandedReleaseRow(
                context = context,
                release = release,
                poster = posters[release.movieId],
                isOnline = isOnline,
                primary = index == 0,
                posterHeight = minOf(
                    rowHeight,
                    if (index == 0) {
                        PlanWidgetDimensions.PrimaryReleasePosterHeight
                    } else {
                        PlanWidgetDimensions.SecondaryReleasePosterHeight
                    },
                ),
                modifier = GlanceModifier.defaultWeight(),
            )
            if (index < releases.lastIndex) WidgetDivider()
        }
        if (!isOnline) {
            Row(
                modifier = GlanceModifier.padding(
                    start = PlanWidgetDimensions.ContentPadding,
                    end = PlanWidgetDimensions.ContentPadding,
                    bottom = PlanWidgetDimensions.CachedFooterBottomPadding,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WidgetIcon(Icons.Outlined.WifiOff, PlanWidgetDimensions.MetadataIconSize, PlanWidgetTheme.Secondary)
                Spacer(GlanceModifier.width(PlanWidgetDimensions.MetadataIconSpacing))
                Text(
                    text = context.getString(R.string.widget_locally_stored_releases),
                    maxLines = 1,
                    style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 9.sp),
                )
            }
        }
    }
}

@Composable
private fun ExpandedReleaseRow(
    context: Context,
    release: TrackedRelease,
    poster: Bitmap?,
    isOnline: Boolean,
    primary: Boolean,
    posterHeight: Dp,
    modifier: GlanceModifier,
) {
    val emphasizeCountdown = release.releaseDate.isImminentWidgetRelease()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(movieAction(context, release.movieId))
            .padding(horizontal = PlanWidgetDimensions.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetPoster(
            title = release.title,
            poster = poster,
            width = posterHeight * if (primary) {
                PlanWidgetDimensions.RELEASE_POSTER_ASPECT_RATIO
            } else {
                PlanWidgetDimensions.SECONDARY_RELEASE_POSTER_ASPECT_RATIO
            },
            height = posterHeight,
        )
        Spacer(GlanceModifier.width(PlanWidgetDimensions.ReleaseRowSpacing))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = release.title,
                maxLines = 1,
                style = TextStyle(
                    color = if (primary) PlanWidgetTheme.Primary else PlanWidgetTheme.Secondary,
                    fontSize = if (primary) 12.sp else 11.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(GlanceModifier.height(PlanWidgetDimensions.ExpandedReleaseTitleSpacing))
            WidgetReleaseMetadata(
                date = release.releaseDate.toWidgetReleaseDate(context, isOnline, includeRelative = false),
                reminder = release.reminder.toWidgetLabel(context).takeIf { primary && isOnline },
                itemSpacing = PlanWidgetDimensions.ExpandedReleaseMetadataItemSpacing,
            )
        }
        Spacer(GlanceModifier.width(PlanWidgetDimensions.ContentSpacing))
        release.releaseDate?.let {
            Text(
                text = it.toWidgetReleaseCountdown(context),
                maxLines = 1,
                style = TextStyle(
                    color = if (emphasizeCountdown) PlanWidgetTheme.Accent else PlanWidgetTheme.Secondary,
                    fontSize = 11.sp,
                    fontWeight = if (emphasizeCountdown) FontWeight.Bold else FontWeight.Normal,
                ),
            )
        }
    }
}

@Composable
private fun ReleaseHero(context: Context, release: TrackedRelease, isOnline: Boolean) {
    Column {
        Text(
            text = release.title,
            maxLines = 1,
            style = TextStyle(
                color = PlanWidgetTheme.Primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        Spacer(GlanceModifier.height(PlanWidgetDimensions.ReleaseTitleSpacing))
        WidgetReleaseMetadata(
            date = release.releaseDate.toWidgetReleaseDate(context, isOnline, includeRelative = false),
            reminder = release.reminder.toWidgetLabel(context).takeIf { isOnline },
            cached = !isOnline,
        )
    }
}

@Composable
private fun SecondaryReleaseRow(
    context: Context,
    release: TrackedRelease,
    poster: Bitmap?,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(PlanWidgetDimensions.SecondaryReleaseRowHeight)
            .clickable(movieAction(context, release.movieId))
            .padding(horizontal = PlanWidgetDimensions.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetPoster(
            title = release.title,
            poster = poster,
            width = PlanWidgetDimensions.MediumReleasePosterWidth,
            height = PlanWidgetDimensions.MediumReleasePosterHeight,
        )
        Spacer(GlanceModifier.width(PlanWidgetDimensions.SecondaryReleaseRowSpacing))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = release.title,
                maxLines = 1,
                style = TextStyle(
                    color = PlanWidgetTheme.Secondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = release.dateLabel(context, true),
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 9.sp),
            )
        }
        release.releaseDate?.let {
            Text(
                text = it.toWidgetReleaseCountdown(context, useMonths = false),
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
    isOnline: Boolean,
    modifier: GlanceModifier,
) {
    val emphasizeCountdown = isOnline && release.releaseDate.isImminentWidgetRelease()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(movieAction(context, release.movieId))
            .padding(
                start = PlanWidgetDimensions.ContentPadding,
                top = PlanWidgetDimensions.CompactTopPadding,
                end = PlanWidgetDimensions.ContentPadding,
                bottom = PlanWidgetDimensions.CompactBottomPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetPoster(title = release.title, poster = poster)
        Spacer(GlanceModifier.width(PlanWidgetDimensions.ContentSpacing))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = release.title,
                maxLines = if (LocalSize.current.height >= PlanWidgetDimensions.CompactTwoLineMinHeight) 2 else 1,
                style = TextStyle(
                    color = PlanWidgetTheme.Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(GlanceModifier.height(PlanWidgetDimensions.CompactTitleSpacing))
            Text(
                text = release.releaseDate.toCompactWidgetReleaseDate(context, isOnline),
                maxLines = 1,
                style = TextStyle(
                    color = if (emphasizeCountdown) PlanWidgetTheme.Accent else PlanWidgetTheme.Secondary,
                    fontSize = 11.sp,
                    fontWeight = if (emphasizeCountdown) FontWeight.Medium else FontWeight.Normal,
                ),
            )
        }
    }
}

private fun TrackedRelease.dateLabel(context: Context, isOnline: Boolean): String =
    releaseDate.toWidgetReleaseDate(context, isOnline)

private const val RELEASES_TAB = "releases"
private const val MAX_DATE = "9999-12-31"
private const val MAX_VISIBLE_RELEASES = 3

private fun List<TrackedRelease>.visibleWidgetReleases(): List<TrackedRelease> =
    sortedBy { it.releaseDate?.takeUnless(String::isBlank) ?: MAX_DATE }.take(MAX_VISIBLE_RELEASES)
