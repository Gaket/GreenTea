package ru.gaket.themoviedb.screens.movies.mvu

import ru.gaket.themoviedb.screens.movies.common.MoviesAdapter
import timber.log.Timber

object LogginUtils {

  fun logCurrentState(moviesAdapter : MoviesAdapter) {
    Timber.d("Here is the latest state: ${MutableList(3000) { moviesAdapter.currentList }}")
  }

}
