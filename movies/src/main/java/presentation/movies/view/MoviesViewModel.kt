package ru.gaket.themoviedb.presentation.movies.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.gaket.greentea.GreenTeaViewModel
import presentation.movies.WebNavigator
import ru.gaket.themoviedb.model.movies.repositories.MoviesRepository

class MoviesViewModel(
  dependencies: MoviesFeature.Dependencies
) : GreenTeaViewModel<MoviesFeature.State, MoviesFeature.Message, MoviesFeature.Dependencies>(
  init = MoviesFeature.initialUpdate,
  update = MoviesFeature.Logic::update,
  dependencies = dependencies
)

class MoviesVmFactory(private val repo: MoviesRepository, private val navigator: WebNavigator) :
  ViewModelProvider.NewInstanceFactory() {

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return MoviesViewModel(MoviesFeature.Dependencies(repository = repo, navigator = navigator)) as T
  }
}
