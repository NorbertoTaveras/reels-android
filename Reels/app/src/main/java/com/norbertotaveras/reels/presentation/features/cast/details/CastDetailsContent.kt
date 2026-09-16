package com.norbertotaveras.reels.presentation.features.cast.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditDataModel
import com.norbertotaveras.reels.extensions.toDisplayAge
import com.norbertotaveras.reels.extensions.toDisplayTmdbDate
import com.norbertotaveras.reels.presentation.ui.components.movie.PosterCarousel
import com.norbertotaveras.reels.presentation.ui.components.movie.PosterCarouselItem
import com.norbertotaveras.reels.presentation.ui.components.common.SectionHeader
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme

@Composable
fun CastDetailsContent(
    name: String,
    profileUrl: String?,
    biography: String,
    knownForDepartment: String?,
    knownFor: List<PersonMovieCreditDataModel>,
    birthday: String?,
    placeOfBirth: String?,
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = ReelsSpacing.xxLarge),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.section)
    ) {
        item {
            CastHeroHeader(
                name = name,
                profileUrl = profileUrl,
                knownForDepartment = knownForDepartment,
                isFavorite = isFavorite,
                onBackClick = onBackClick,
                onFavoriteClick = onFavoriteClick
            )
        }

        item {
            CastQuickInfoSection(
                birthday = birthday,
                placeOfBirth = placeOfBirth
            )
        }

        item {
            BiographySection(biography = biography)
        }

        item {
            KnownForPreviewSection(
                knownFor = knownFor,
                onMovieClick = onMovieClick
            )
        }

        item {
            PersonalInfoSection(
                birthday = birthday,
                knownForDepartment = knownForDepartment,
                alsoKnownAs = name,
                knownCreditsCount = knownFor.size
            )
        }
    }
}

@Composable
private fun CastHeroHeader(
    name: String,
    profileUrl: String?,
    knownForDepartment: String?,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ReelsDimensions.castDetailsHeroHeight)
    ) {
        AsyncImage(
            model = profileUrl,
            contentDescription = name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.25f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        FilledIconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = ReelsSpacing.screenHorizontal,
                    top = ReelsDimensions.castDetailsHeroActionTopPadding
                )
                .size(ReelsDimensions.castDetailsHeroActionButtonSize),
            onClick = onBackClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Navigate back"
            )
        }

        FilledIconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    end = ReelsSpacing.screenHorizontal,
                    top = ReelsDimensions.castDetailsHeroActionTopPadding
                )
                .size(ReelsDimensions.castDetailsHeroActionButtonSize),
            onClick = onFavoriteClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                contentColor = if (isFavorite) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        ) {
            Icon(
                imageVector = if (isFavorite) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Outlined.FavoriteBorder
                },
                contentDescription = if (isFavorite) {
                    "Remove from favorites"
                } else {
                    "Add to favorites"
                }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = ReelsSpacing.screenHorizontal)
                .padding(bottom = ReelsSpacing.xLarge),
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (!knownForDepartment.isNullOrBlank()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Work,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.castDetailsDepartmentIconSize),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = knownForDepartment,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CastQuickInfoSection(
    birthday: String?,
    placeOfBirth: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = ReelsSpacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.castDetailsQuickInfoSpacing)
    ) {
        if (!birthday.isNullOrBlank()) {
            CastInfoRow(
                icon = Icons.Filled.CalendarToday,
                label = "Birthday",
                value = birthday.toDisplayTmdbDate().orEmpty()
            )
        }

        if (!placeOfBirth.isNullOrBlank()) {
            CastInfoRow(
                icon = Icons.Filled.LocationOn,
                label = "Birthplace",
                value = placeOfBirth
            )
        }
    }
}

@Composable
private fun CastInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.castDetailsInfoIconContainerSize),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
            )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.castDetailsInfoIconSize)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun BiographySection(
    biography: String,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 5,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val displayText = biography.ifBlank { "No biography available yet." }
    val canToggle = biography.length > 260

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsSpacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
    ) {
        Text(
            text = "Biography",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Box {
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines,
                overflow = TextOverflow.Ellipsis
            )

            if (canToggle && !isExpanded) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(ReelsDimensions.castDetailsBiographyFadeHeight)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background.copy(alpha = 0f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )
            }
        }

        if (canToggle) {
            Surface(
                modifier = Modifier.clickable {
                    isExpanded = !isExpanded
                },
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = ReelsSpacing.medium,
                        vertical = ReelsSpacing.small
                    ),
                    horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isExpanded) "Show less" else "Read more",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Icon(
                        imageVector = if (isExpanded) {
                            Icons.Filled.KeyboardArrowUp
                        } else {
                            Icons.Filled.KeyboardArrowDown
                        },
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.castDetailsBiographyToggleIconSize)
                    )
                }
            }
        }
    }
}



@Composable
private fun KnownForPreviewSection(
    knownFor: List<PersonMovieCreditDataModel>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (knownFor.isEmpty()) return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)
    ) {
        SectionHeader(
            modifier = Modifier.padding(horizontal = ReelsSpacing.screenHorizontal),
            title = "Known For"
        )

        PosterCarousel(
            items = knownFor.map { movie ->
                PosterCarouselItem(
                    id = movie.id,
                    title = movie.title,
                    posterUrl = movie.posterPath.toTmdbImageUrl(),
                    releaseYear = movie.releaseDate?.take(4)
                )
            },
            onMovieClick = onMovieClick
        )
    }
}


@Composable
private fun PersonalInfoSection(
    birthday: String?,
    knownForDepartment: String?,
    knownCreditsCount: Int,
    alsoKnownAs: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsSpacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
    ) {
        Text(
            text = "Personal Info",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
            )
        ) {
            Column {
                PersonalInfoRow(
                    label = "Age",
                    value = birthday.toDisplayAge() ?: "Unknown"
                )

                PersonalInfoDivider()

                PersonalInfoRow(
                    label = "Known Credits",
                    value = knownCreditsCount.toString()
                )

                if (!alsoKnownAs.isNullOrBlank()) {
                    PersonalInfoDivider()

                    PersonalInfoRow(
                        label = "Also Known As",
                        value = alsoKnownAs
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = ReelsSpacing.large,
                vertical = ReelsSpacing.medium
            ),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun PersonalInfoDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
    )
}

@Preview(showBackground = true)
@Composable
private fun CastDetailsContentPreview() {
    ReelsTheme {
        CastDetailsContent(
            name = "Timothée Chalamet",
            profileUrl = null,
            biography = "Timothée Chalamet is an American and French actor known for emotionally precise performances across independent dramas and large-scale films.",
            knownForDepartment = "Acting",
            knownFor = emptyList(),
            birthday = "December 27, 1995",
            placeOfBirth = "New York City, New York, USA",
            onBackClick = {},
            onMovieClick = {}
        )
    }
}
