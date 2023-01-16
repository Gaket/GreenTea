package ca.gaket.themoviedb.screens.movies.mvu

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ca.gaket.tea.GreenTeaViewModel
import ca.gaket.themoviedb.data.common.WebNavigator
import ca.gaket.themoviedb.data.repositories.MoviesRepository
import ca.gaket.tools.analytics.AnalyticsService

class MoviesViewModel(
  dependencies: MoviesFeature.Dependencies,
  savedStateHandle: SavedStateHandle
) : GreenTeaViewModel<MoviesFeature.State, MoviesFeature.Message, MoviesFeature.Dependencies>(
  init = MoviesFeature.Logic.initialUpdate,
  update = MoviesFeature.Logic::update,
  restore = MoviesFeature.Logic::restore,
  dependencies = dependencies,
  savedStateHandle = savedStateHandle
)

class MoviesVmFactory(
  private val repo: MoviesRepository,
  private val navigator: WebNavigator,
  private val analyticsService: AnalyticsService
) :
  AbstractSavedStateViewModelFactory() {

  override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
    return MoviesViewModel(
      dependencies = MoviesFeature.Dependencies(
        repository = repo,
        navigator = navigator,
        analyticsService = analyticsService
      ),
      savedStateHandle = handle
    ) as T
  }
}
