package ru.gaket.themoviedb.screens.movies.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import ru.gaket.themoviedb.data.common.WebNavigator
import ru.gaket.themoviedb.data.common.isSuccess
import ru.gaket.themoviedb.data.entities.Movie
import ru.gaket.themoviedb.data.repositories.MoviesRepository
import java.util.concurrent.CancellationException

class OldMoviesViewModel(val moviesRepository: MoviesRepository, val navigator: WebNavigator) : ViewModel() {

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
                val result = moviesRepository.searchMovies(it)
                if (result.isSuccess()) {
                    if (result.value.isEmpty()) {
                        EmptyResult
                    } else {
                        ValidResult(result.value)
                    }
                } else {
                    if (result.error is CancellationException) {
                        throw result.error
                    } else {
                        Log.w(OldMoviesViewModel::class.java.name, result.error)
                        ErrorResult(result.error)
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
    class Factory(private val repo: MoviesRepository, private val navigator: WebNavigator) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return OldMoviesViewModel(moviesRepository = repo, navigator = navigator) as T
        }
    }
}
