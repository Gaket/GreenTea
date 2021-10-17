package ru.gaket.themoviedb.model.common

sealed class Screen {
    object Movies : Screen()
    class MovieDetails(val movieId: Int) : Screen()
}
