package ca.gaket.themoviedb.screens.movies.mvu

import ca.gaket.themoviedb.R
import ca.gaket.themoviedb.data.common.Text
import ca.gaket.themoviedb.data.common.Try
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalTime

class MoviesFeatureTest {

  @Test
  fun `Search query update leads to loading state`() {
    val state = initialState()
    val testQuery = "test"
    val message = MoviesFeature.Message.SearchUpdated(testQuery, LocalTime.NOON)

    val update = MoviesFeature.Logic.update(message, state)

    assertThat(update.state.isLoading).isTrue
    assertThat(update.commands).contains(MoviesFeature.Effects.GetMovies(testQuery))
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
