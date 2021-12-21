package ca.gaket.themoviedb.data.repositories

import android.util.Log
import ca.gaket.themoviedb.BuildConfig
import ca.gaket.themoviedb.data.common.Try
import ca.gaket.themoviedb.data.entities.Movie
import ca.gaket.themoviedb.data.network.MovieNetworkModel
import ca.gaket.themoviedb.data.network.MoviesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

/**
 * Repository providing data about [Movie]
 */
class MoviesRepository(private val moviesApi: MoviesApi) {

  /**
   * Search [Movie]s for the given [query] string
   */
  @OptIn(FlowPreview::class)
  internal suspend fun searchMovies(query: String, page: Int = 1): Try<List<Movie>> {
    return try {
      Try.Success(
        withContext(Dispatchers.IO) {
          flow {
            val firstResponse = moviesApi.searchMovie(BuildConfig.API_KEY, query, page)
            emit(firstResponse)
            val secondResponse = moviesApi.searchMovie(BuildConfig.API_KEY, query, page + 1)
            emit(secondResponse)
            val thirdResponse = moviesApi.searchMovie(BuildConfig.API_KEY, query, page + 2)
            emit(thirdResponse)
            emit(firstResponse)
          }
        }
          .flowOn(Dispatchers.IO)
          .onEach { Log.d(MoviesRepository::class.java.name, it.movies.toString()) }
          .flatMapMerge { it.movies.asFlow() }
          .map { Movie(it.id, it.title, getPosterUrl(it)) }
          .toList())
    } catch (ex: Exception) {
      Try.Failure(ex)
    }
  }

  private fun getPosterUrl(it: MovieNetworkModel) = "${BuildConfig.BASE_IMAGE_URL}${it.posterPath}"
}
