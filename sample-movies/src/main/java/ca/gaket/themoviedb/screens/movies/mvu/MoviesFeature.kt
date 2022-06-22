package ca.gaket.themoviedb.screens.movies.mvu

import ca.gaket.tea.runtime.coroutines.Effect
import ca.gaket.tea.runtime.coroutines.Update
import ca.gaket.tea.runtime.coroutines.adaptIdle
import ca.gaket.tea.runtime.coroutines.noEffects
import ca.gaket.tea.runtime.coroutines.with
import ca.gaket.themoviedb.R
import ca.gaket.themoviedb.data.common.Screen
import ca.gaket.themoviedb.data.common.Text
import ca.gaket.themoviedb.data.common.Try
import ca.gaket.themoviedb.data.common.WebNavigator
import ca.gaket.themoviedb.data.common.commands.NavigationEffects
import ca.gaket.themoviedb.data.entities.Movie
import ca.gaket.themoviedb.data.repositories.MoviesRepository
import ca.gaket.tools.analytics.AnalyticsService
import ca.gaket.tools.effects.AnalyticsEffects
import java.time.Duration
import java.time.LocalTime

object MoviesFeature {

  data class State(
    val isLoading: Boolean?,
    val movies: List<Movie>?,
    val message: Text?,
  )

  sealed class Message {
    // user
    data class SearchUpdated(val query: String, val time: LocalTime) : Message()
    data class MovieClicked(val movie: Movie) : Message()

    // system
//    data class MoviesResponse(val response: Try<List<Movie>>) : Message()

    // subscriptions
    // here we could have messages from subscriptions
  }

  object Logic {

    val initialUpdate = State(
      isLoading = null,
      movies = null,
      message = null,
    ) with noEffects<Message, Dependencies>()

    fun update(message: Message, state: State): Update<State, Message, Dependencies> = state with noEffects()
  }

  object Effects {

//    data class GetMovies(val query: String) : Effect<Dependencies, Message> by Effect.single({ deps ->
//      val movies = deps.repository.searchMovies(query)
//      return@single Message.MoviesResponse(movies)
//    })

  }

  class Dependencies(
    val repository: MoviesRepository,
    val navigator: WebNavigator,
    val analyticsService: AnalyticsService
  )

}
