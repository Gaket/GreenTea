package ru.gaket.themoviedb.screens.movies.mvu

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.data.common.Text
import ru.gaket.themoviedb.data.common.Try
import java.time.LocalTime

class MoviesFeatureTest {

  @Test
  fun `Search query update leads to loading state`() {
    val state = initialState()
    val testQuery = "test"
    val message = MoviesFeature.Message.SearchUpdated(testQuery, LocalTime.NOON)

    val update = MoviesFeature.Logic.update(message, state)

    assertThat(update.state.isLoading).isTrue
    assertThat(update.commands).contains(MoviesFeature.Commands.GetMovies(testQuery))
  }

  @Test
  fun `Show welcoming message on the start`() {
    val state = initialState()

    assertThat(state.message).isEqualTo(Text.ResText(R.string.movies_placeholder))
  }

  @Test
  fun `Show not found message and clear movies if no movies found for query`() {
    val state = loadingState()
    val message = MoviesFeature.Message.MoviesResponse(Try.Success(emptyList()))

    val update = MoviesFeature.Logic.update(message, state)

    assertThat(update.state.message).isEqualTo(Text.ResText(R.string.empty_result))
    assertThat(update.state.movies).isEmpty()
  }

  @Test
  fun `Show generic error in case of Movies Response error`() {
    val state = loadingState()
    val message = MoviesFeature.Message.MoviesResponse(Try.Failure(RuntimeException()))

    val update = MoviesFeature.Logic.update(message, state)

    assertThat(update.state.message).isEqualTo(Text.ResText(R.string.search_error))
    assertThat(update.state.movies).isEmpty()
  }

  @Test
  fun `Hide loading after Movies response`() {
    val state = loadingState()
    val message = MoviesFeature.Message.MoviesResponse(Try.Success(emptyList()))

    val update = MoviesFeature.Logic.update(message, state)

    assertThat(update.state.isLoading).isFalse
  }


  private fun initialState() = MoviesFeature.Logic.initialUpdate.state
  private fun loadingState() = initialState().copy(isLoading = true)

}
