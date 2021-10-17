package ru.gaket.themoviedb.screens.movies.mvvm

import ru.gaket.themoviedb.model.entities.Movie

/**
 * Class containing the result of the [Movie] request
 */
sealed class MoviesResult
class ValidResult(val result: List<Movie>) : MoviesResult()
object EmptyResult : MoviesResult()
object EmptyQuery : MoviesResult()
class ErrorResult(val e: Throwable) : MoviesResult()
object TerminalError : MoviesResult()
