package ru.gaket.themoviedb.presentation.movies

sealed class Screen {
    object Movies : Screen()
    class MovieDetails(val movieId: Int) : Screen()
}
