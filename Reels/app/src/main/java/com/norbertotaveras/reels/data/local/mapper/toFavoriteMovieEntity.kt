package com.norbertotaveras.reels.data.local.mapper

import com.norbertotaveras.reels.domain.model.favorite.FavoriteMovieDataModel
import com.norbertotaveras.reels.domain.model.genre.GenreDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.data.local.entity.FavoriteMovieEntity

fun MovieDetailsDataModel.toFavoriteMovieEntity(
    favoritedAt: Long = System.currentTimeMillis()
): FavoriteMovieEntity {
    return FavoriteMovieEntity(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        runtime = runtime,
        voteAverage = voteAverage,
        voteCount = voteCount,
        originalLanguage = originalLanguage,
        popularity = popularity,
        genreNames = genres.map { genre -> genre.name },
        favoritedAt = favoritedAt
    )
}

fun FavoriteMovieEntity.toMovieDataModel(): MovieDataModel {
    return MovieDataModel(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        genreIds = emptyList()
    )
}

fun FavoriteMovieEntity.toMovieDetailsDataModel(): MovieDetailsDataModel {
    return MovieDetailsDataModel(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        runtime = runtime,
        voteAverage = voteAverage,
        originalLanguage = originalLanguage,
        popularity = popularity,
        voteCount = voteCount,
        genres = genreNames.mapIndexed { index, name ->
            GenreDataModel(
                id = index,
                name = name
            )
        }
    )
}

fun FavoriteMovieEntity.toFavoriteMovieDataModel(): FavoriteMovieDataModel {
    return FavoriteMovieDataModel(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        runtime = runtime,
        voteAverage = voteAverage,
        voteCount = voteCount,
        originalLanguage = originalLanguage,
        popularity = popularity,
        genreNames = genreNames,
        favoritedAt = favoritedAt
    )
}

