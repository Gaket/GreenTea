package ru.gaket.themoviedb.screens.movies.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import ru.gaket.themoviedb.data.common.Try
import ru.gaket.themoviedb.data.common.WebNavigator
import ru.gaket.themoviedb.data.entities.Movie
import ru.gaket.themoviedb.data.repositories.MoviesRepository

private const val TEXT_ENTERED_DEBOUNCE_MILLIS = 500L

class OldMoviesViewModel(val moviesRepository: MoviesRepository, val navigator: WebNavigator) : ViewModel() {

  private val queryFlow = MutableStateFlow("")

  private val _searchResult = MutableStateFlow<MoviesResult>(MoviesResult.EmptyQuery)
  val searchResult: LiveData<MoviesResult>
    get() = _searchResult
      .asLiveData(viewModelScope.coroutineContext)

  init {
    viewModelScope.launch {
      queryFlow
        .sample(TEXT_ENTERED_DEBOUNCE_MILLIS)
        .onEach { _searchResult.value = MoviesResult.Loading }
        .mapLatest(::handleQuery)
        .collect { state -> _searchResult.value = state }
    }
  }

  fun onNewQuery(query: String) {
    queryFlow.value = query
  }

  fun onMovieAction(it: Movie) {
    navigator.navigateTo("https://www.themoviedb.org/movie/${it.id}")
  }

  private suspend fun handleQuery(query: String): MoviesResult {
    return if (query.isEmpty()) {
      MoviesResult.EmptyQuery
    } else {
      handleSearchMovie(query)
    }
  }

  private suspend fun handleSearchMovie(query: String): MoviesResult {
    return when (val moviesResult = moviesRepository.searchMovies(query)) {
      is Try.Failure -> MoviesResult.ErrorResult(IllegalArgumentException("Search movies from server error!"))
      is Try.Success -> if (moviesResult.value.isEmpty()) MoviesResult.EmptyResult else MoviesResult.SuccessResult(
        moviesResult.value
      )
    }
  }

  @Suppress("UNCHECKED_CAST")
  class Factory(private val repo: MoviesRepository, private val navigator: WebNavigator) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return OldMoviesViewModel(moviesRepository = repo, navigator = navigator) as T
    }
  }
}
