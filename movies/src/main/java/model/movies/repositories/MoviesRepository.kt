package ru.gaket.themoviedb.model.movies.repositories

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import ru.gaket.themoviedb.BuildConfig
import ru.gaket.themoviedb.model.movies.entities.Movie
import ru.gaket.themoviedb.model.movies.network.MovieNetworkModel
import ru.gaket.themoviedb.model.movies.network.MoviesApi

/**
 * Repository providing data about [Movie]
 */
class MoviesRepository(private val moviesApi: MoviesApi) {

  /**
   * Search [Movie]s for the given [query] string
   */
  @ExperimentalCoroutinesApi
  @FlowPreview
  internal suspend fun searchMovies(query: String, page: Int = 1): List<Movie> {
    return withContext(Dispatchers.IO) {
      flowOf(
          moviesApi.searchMovie(BuildConfig.API_KEY, query, page)
      )
    }
        .flowOn(Dispatchers.IO)
        .onEach { Log.d(MoviesRepository::class.java.name, it.movies.toString()) }
        .flatMapMerge { it.movies.asFlow() }
        .map { Movie(it.id, it.title, getPosterUrl(it)) }
        .toList()
  }

  private fun getPosterUrl(it: MovieNetworkModel) =  "${BuildConfig.BASE_IMAGE_URL}${it.posterPath}"
}