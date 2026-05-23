package com.norbertotaveras.reels.presentation.features.movies.search

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.norbertotaveras.reels.extensions.toRelativeReleaseAge
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.EmptyState
import com.norbertotaveras.reels.presentation.ui.components.common.InlineRetryState
import com.norbertotaveras.reels.presentation.ui.components.movie.MovieResultRow
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchUiState,
    movies: LazyPagingItems<MovieDataModel>,
    onEvent: (SearchEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = ReelsSpacing.screenHorizontal)
    ) {
        Text(
            modifier = Modifier.padding(top = ReelsSpacing.xLarge),
            text = "Search",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = ReelsSpacing.large),
            value = state.query,
            onValueChange = { query ->
                onEvent(SearchEvent.QueryChanged(query = query))
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            placeholder = {
                Text(text = "Search movies")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (state.query.isNotBlank()) {
                    IconButton(
                        onClick = {
                            onEvent(SearchEvent.ClearQueryClicked)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onEvent(SearchEvent.SearchSubmitted)
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.36f),
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = ReelsSpacing.large),
            contentPadding = PaddingValues(bottom = ReelsSpacing.xxLarge),
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
        ) {
            if (state.recentSearches.isNotEmpty()) {
                item {
                    RecentSearchesSection(
                        recentSearches = state.recentSearches,
                        onEvent = onEvent
                    )
                }
            }

            item {
                Text(
                    text = "Search Results",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            when {
                state.query.length < 2 -> {
                    item {
                        EmptyState(
                            title = "Search for a movie",
                            message = "Enter at least two characters to find movies from TMDB."
                        )
                    }
                }

                movies.loadState.refresh is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                movies.loadState.refresh is LoadState.Error -> {
                    item {
                        InlineRetryState(
                            message = "Unable to load search results.",
                            retryLabel = "Retry",
                            onRetry = movies::retry
                        )
                    }
                }

                movies.itemCount == 0 -> {
                    item {
                        EmptyState(
                            title = "No movies found",
                            message = "Try a different title or check the spelling."
                        )
                    }
                }

                else -> {
                    items(
                        count = movies.itemCount,
                        key = { index -> movies[index]?.id ?: index }
                    ) { index ->
                        val movie = movies[index]

                        if (movie != null) {
                            MovieResultRow(
                                title = movie.title,
                                posterUrl = movie.posterPath.toTmdbImageUrl(),
                                releaseYear = movie.releaseDate.toReleaseYear(),
                                secondaryMetadata = movie.releaseDate.toRelativeReleaseAge(),
                                genres = movie.genreIds
                                    .mapNotNull { genreId -> state.genreNamesById[genreId] }
                                    .take(2),
                                rating = String.format("%.1f", movie.voteAverage),
                                onClick = {
                                    onEvent(SearchEvent.MovieClicked(movieId = movie.id))
                                }
                            )
                        }
                    }

                    if (movies.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    if (movies.loadState.append is LoadState.Error) {
                        item {
                            InlineRetryState(
                                message = "Unable to load more results.",
                                retryLabel = "Retry",
                                onRetry = movies::retry
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentSearchesSection(
    recentSearches: List<String>,
    onEvent: (SearchEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Clear all",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onEvent(SearchEvent.ClearRecentSearchesClicked)
                }
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
        ) {
            recentSearches.forEach { query ->
                Surface(
                    onClick = {
                        onEvent(SearchEvent.RecentSearchClicked(query = query))
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = ReelsSpacing.medium,
                            vertical = ReelsSpacing.small
                        ),
                        text = query,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

            }
        }
    }
}
