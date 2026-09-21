package com.norbertotaveras.reels.presentation.ui.components.movie

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsLightGenreChipContainer
import com.norbertotaveras.reels.presentation.ui.theme.ReelsGenreChipContainer
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme
import com.norbertotaveras.reels.presentation.ui.theme.isReelsLightTheme

@Composable
fun GenreChipRow(
    genres: List<String>,
    modifier: Modifier = Modifier,
) {
    val visibleGenres = genres.filter { it.isNotBlank() }

    if (visibleGenres.isEmpty()) return

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.detailsGenreChipSpacing)
    ) {
        visibleGenres.forEach { genre ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isReelsLightTheme()) {
                    ReelsLightGenreChipContainer
                } else {
                    ReelsGenreChipContainer
                },
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Text(
                    modifier = Modifier.padding(
                        horizontal = ReelsDimensions.detailsGenreChipHorizontalPadding,
                        vertical = ReelsDimensions.detailsGenreChipVerticalPadding
                    ),
                    text = genre,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GenreChipRowPreview() {
    ReelsTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(ReelsSpacing.large)
        ) {
            GenreChipRow(
                genres = listOf("Science Fiction", "Adventure", "Drama")
            )
        }
    }
}
