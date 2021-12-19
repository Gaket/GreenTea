package ru.gaket.themoviedb.screens.movies.mvvm

import ru.gaket.themoviedb.data.entities.Movie

/**
 * Class containing the result of the [Movie] request
 */
/**
 * Class containing the result of the [SearchMovie] request
 */
sealed class MoviesResult {
  object Loading : MoviesResult()
  object EmptyResult : MoviesResult()
  object EmptyQuery : MoviesResult()
  data class SuccessResult(val result: List<Movie>) : MoviesResult()
  data class ErrorResult(val e: Throwable) : MoviesResult()
}
