package ru.gaket.themoviedb.presentation.movies.viewmodel

import ru.gaket.themoviedb.model.movies.entities.Movie

/**
 * Class containing the result of the [Movie] request
 */
sealed class MoviesResult
class ValidResult(val result: List<Movie>) : MoviesResult()
object EmptyResult : MoviesResult()
object EmptyQuery : MoviesResult()
class ErrorResult(val e: Throwable) : MoviesResult()
object TerminalError : MoviesResult()