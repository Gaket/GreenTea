package ru.gaket.themoviedb.data.common

sealed class Screen {
    object Movies : Screen()
    class MovieDetails(val movieId: Int) : Screen()
}
