package ca.gaket.themoviedb.screens.movies.mvu

import ca.gaket.tea.runtime.coroutines.Effect
import ca.gaket.tea.runtime.coroutines.Update
import ca.gaket.tea.runtime.coroutines.adaptIdle
import ca.gaket.tea.runtime.coroutines.noCommands
import ca.gaket.tea.runtime.coroutines.with
import ca.gaket.themoviedb.R
import ca.gaket.themoviedb.data.common.Screen
import ca.gaket.themoviedb.data.common.Text
import ca.gaket.themoviedb.data.common.Try
import ca.gaket.themoviedb.data.common.WebNavigator
import ca.gaket.themoviedb.data.common.commands.NavigationCommands
import ca.gaket.themoviedb.data.entities.Movie
import ca.gaket.themoviedb.data.repositories.MoviesRepository
import com.fullstory.FS
import java.time.Duration
import java.time.LocalTime

object MoviesFeature {

  data class State(
    val isLoading: Boolean,
    val movies: List<Movie>,
    val message: Text,
    val lastRequestTime: LocalTime
  )

  sealed class Message {
    // user
    data class SearchUpdated(val query: String, val time: LocalTime) : Message()
    data class MovieClicked(val movie: Movie) : Message()

    // system
    data class MoviesResponse(val response: Try<List<Movie>>) : Message()

    // subscriptions
    // here we could have messages from subscriptions
  }

  object Logic {

    val initialUpdate = State(
      isLoading = false,
      movies = emptyList(),
      message = Text.ResText(R.string.movies_placeholder),
      lastRequestTime = LocalTime.MIN,
    ) with noCommands<Message, Dependencies>()

    fun update(message: Message, state: State): Update<State, Message, Dependencies> =
      when (message) {
        is Message.MovieClicked -> handleMovieClick(message.movie, state)
        is Message.SearchUpdated -> handleSearchUpdate(message.query, message.time, state)
        is Message.MoviesResponse -> handleMoviesResponse(message.response, state)
      }

    private fun handleMoviesResponse(
      response: Try<List<Movie>>,
      state: State
    ): Update<State, Message, Dependencies> =
      when (response) {
        is Try.Failure -> state.copy(
          isLoading = false,
          movies = emptyList(),
          message = Text.ResText(R.string.search_error)
        ) with noCommands()
        is Try.Success -> {
          if (response.value.isEmpty()) {
            state.copy(isLoading = false, movies = emptyList(), message = Text.ResText(R.string.empty_result))
          } else {
            state.copy(isLoading = false, movies = response.value, message = Text.PlainText(""))
          } with noCommands()
        }
      }

    private fun handleMovieClick(movie: Movie, state: State): Update<State, Message, Dependencies> {
      FS.event("Movie Details Requested", mapOf("id" to movie.id))
      return state with
        NavigationCommands.Forward(Screen.MovieDetails(movie.id)).adaptIdle { deps -> deps.navigator }
    }

    private fun handleSearchUpdate(
      query: String,
      currentTime: LocalTime,
      state: State
    ): Update<State, Message, Dependencies> {
      return when {
        query.isEmpty() -> return state.copy(
          isLoading = false,
          movies = emptyList(),
          message = Text.ResText(R.string.movies_placeholder)
        ) with noCommands()
        Duration.between(state.lastRequestTime, currentTime).toMillis() < 500 -> {
          state with noCommands()
        }
        else -> {
          FS.event("Query updated", mapOf("query" to query))
          state.copy(lastRequestTime = currentTime, isLoading = true) with Effects.GetMovies(query)
        }
      }
    }
  }

  object Effects {

    data class GetMovies(val query: String) : Effect<Dependencies, Message> by Effect.single({ deps ->
      val movies = deps.repository.searchMovies(query)
      return@single Message.MoviesResponse(movies)
    })

  }

  class Dependencies(
    val repository: MoviesRepository,
    val navigator: WebNavigator
  )

}
