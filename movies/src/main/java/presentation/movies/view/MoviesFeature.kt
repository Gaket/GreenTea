package ru.gaket.themoviedb.presentation.movies.view

import net.gaket.greentea.runtime.coroutines.Command
import net.gaket.greentea.runtime.coroutines.Update
import net.gaket.greentea.runtime.coroutines.adaptIdle
import net.gaket.greentea.runtime.coroutines.noCommands
import net.gaket.greentea.runtime.coroutines.with
import presentation.movies.WebNavigator
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.model.movies.common.Text
import ru.gaket.themoviedb.model.movies.common.Try
import ru.gaket.themoviedb.model.movies.common.effects.NavigationCommands
import ru.gaket.themoviedb.model.movies.entities.Movie
import ru.gaket.themoviedb.model.movies.repositories.MoviesRepository
import ru.gaket.themoviedb.presentation.movies.Screen
import java.time.Duration
import java.time.LocalTime

object MoviesFeature {

  val initialUpdate = State(
    loading = false,
    movies = emptyList(),
    message = Text.ResText(R.string.movies_placeholder),
    lastRequestTime = LocalTime.MIN,
  ) with noCommands<Message, Dependencies>()

  class Dependencies(
    val repository: MoviesRepository,
    val navigator: WebNavigator
  )

  data class State(
    val loading: Boolean,
    val movies: List<Movie>,
    val message: Text,
    val lastRequestTime: LocalTime
  )

  sealed class Message {
    // user
    data class SearchUpdated(val query: String) : Message()
    data class MovieClicked(val movie: Movie) : Message()

    // system
    data class MoviesResponse(val response: Try<List<Movie>>) : Message()
    // subscriptions
    // here we could have messages from subscriptions
  }

  object Logic {

    fun update(message: Message, state: State): Update<State, Message, Dependencies> =
      when (message) {
        is Message.MovieClicked -> handleMovieClick(message.movie, state)
        is Message.SearchUpdated -> handleSearchUpdate(message.query, state)
        is Message.MoviesResponse -> handleMoviesResponse(message.response, state)
      }

    private fun handleMoviesResponse(
      response: Try<List<Movie>>,
      state: State
    ): Update<State, Message, Dependencies> =
      when (response) {
        is Try.Failure -> state.copy(
          loading = false,
          movies = emptyList(),
          message = Text.ResText(R.string.error_unknown_on_download)
        ) with noCommands()
        is Try.Success -> {
          if (response.value.isEmpty()) {
            state.copy(loading = false, movies = emptyList(), message = Text.ResText(R.string.empty_result))
          } else {
            state.copy(loading = false, movies = response.value, message = Text.PlainText(""))
          } with noCommands()
        }
      }

    private fun handleMovieClick(movie: Movie, state: State): Update<State, Message, Dependencies> {
      return state with NavigationCommands.Forward(Screen.MovieDetails(movie.id)).adaptIdle { deps -> deps.navigator }
    }

    private fun handleSearchUpdate(query: String, state: State): Update<State, Message, Dependencies> {
      val now = LocalTime.now()
      return when {
        query.isEmpty() -> return state.copy(
          loading = false,
          movies = emptyList(),
          message = Text.ResText(R.string.movies_placeholder)
        ) with noCommands()
        Duration.between(state.lastRequestTime, now).toMillis() < 500 -> {
          state with noCommands()
        }
        else -> {
          state.copy(lastRequestTime = now, loading = true) with Commands.GetMovies(query)
        }
      }
    }
  }

  object Commands {

    class GetMovies(query: String) : Command<Dependencies, Message> by Command.single({ deps ->
      val movies = deps.repository.searchMovies(query)
      return@single Message.MoviesResponse(movies)
    })

  }

}
