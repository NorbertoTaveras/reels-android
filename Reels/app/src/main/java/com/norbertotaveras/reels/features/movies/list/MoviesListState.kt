package com.norbertotaveras.reels.features.movies.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.norbertotaveras.reels.model.constant.MovieCategory

class MoviesListState {
    var category by mutableStateOf(MovieCategory.POPULAR)
    var language by mutableStateOf("en-US")
}
