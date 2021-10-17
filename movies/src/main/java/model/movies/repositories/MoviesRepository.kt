package ru.gaket.themoviedb.model.movies.repositories

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import ru.gaket.themoviedb.BuildConfig
import ru.gaket.themoviedb.model.movies.common.Try
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
    @OptIn(FlowPreview::class)
    internal suspend fun searchMovies(query: String, page: Int = 1): Try<List<Movie>> {
        return try {
            Try.Success(
                withContext(Dispatchers.IO) {
                    flowOf(
                        moviesApi.searchMovie(BuildConfig.API_KEY, query, page)
                    )
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
