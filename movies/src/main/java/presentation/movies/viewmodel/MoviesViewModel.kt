package ru.gaket.themoviedb.presentation.movies.viewmodel

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import ru.gaket.themoviedb.model.movies.entities.Movie
import ru.gaket.themoviedb.model.movies.repositories.MoviesRepository
import ru.gaket.themoviedb.ru.gaket.themoviedb.presentation.movies.Navigator
import ru.gaket.themoviedb.ru.gaket.themoviedb.presentation.movies.viewmodel.Loading
import ru.gaket.themoviedb.ru.gaket.themoviedb.presentation.movies.viewmodel.Ready
import ru.gaket.themoviedb.ru.gaket.themoviedb.presentation.movies.viewmodel.SearchState
import java.util.concurrent.CancellationException

class MoviesViewModel(val moviesRepository: MoviesRepository, val navigator: Navigator) : ViewModel() {

  @ExperimentalCoroutinesApi
  val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

  private val _searchState = MutableLiveData<SearchState>()

  @FlowPreview
  @ExperimentalCoroutinesApi
  private val _searchResult = queryChannel
      .asFlow()
      .debounce(500)
      .onEach {
        _searchState.value = Loading
      }
      .mapLatest {
        if (it.isEmpty()) {
          EmptyQuery
        } else {
          try {
            val result = moviesRepository.searchMovies(it)
            if (result.isEmpty()) {
              EmptyResult
            } else {
              ValidResult(result)
            }
          } catch (e: Throwable) {
            if (e is CancellationException) {
              throw e
            } else {
              Log.w(MoviesViewModel::class.java.name, e)
              ErrorResult(e)
            }
          }
        }
      }
      .onEach {
        _searchState.value = Ready
      }
      .catch { emit(TerminalError) }
      .asLiveData(viewModelScope.coroutineContext)

  @ExperimentalCoroutinesApi
  @FlowPreview
  val searchResult: LiveData<MoviesResult>
    get() = _searchResult

  val searchState: LiveData<SearchState>
    get() = _searchState

  fun onMovieAction(it: Movie) {
    navigator.navigateTo("https://www.themoviedb.org/movie/${it.id}")
  }

  @Suppress("UNCHECKED_CAST")
  class Factory(private val repo: MoviesRepository, private val navigator: Navigator) :
      ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      return MoviesViewModel(moviesRepository = repo, navigator = navigator) as T
    }
  }
}
