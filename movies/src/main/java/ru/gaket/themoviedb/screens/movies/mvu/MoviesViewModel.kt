package ru.gaket.themoviedb.screens.movies.mvu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.gaket.tea.GreenTeaViewModel
import ru.gaket.themoviedb.data.common.WebNavigator
import ru.gaket.themoviedb.data.repositories.MoviesRepository

class MoviesViewModel(
  dependencies: MoviesFeature.Dependencies
) : GreenTeaViewModel<MoviesFeature.State, MoviesFeature.Message, MoviesFeature.Dependencies>(
  init = MoviesFeature.Logic.initialUpdate,
  update = MoviesFeature.Logic::update,
  dependencies = dependencies
)

class MoviesVmFactory(private val repo: MoviesRepository, private val navigator: WebNavigator) :
  ViewModelProvider.NewInstanceFactory() {

  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return MoviesViewModel(MoviesFeature.Dependencies(repository = repo, navigator = navigator)) as T
  }
}
