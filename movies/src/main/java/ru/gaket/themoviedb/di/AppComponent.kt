package ru.gaket.themoviedb.di

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.gaket.themoviedb.BuildConfig
import ru.gaket.themoviedb.model.common.WebNavigator
import ru.gaket.themoviedb.model.network.MoviesApi
import ru.gaket.themoviedb.model.repositories.MoviesRepository
import ru.gaket.themoviedb.screens.movies.mvu.MoviesVmFactory
import ru.gaket.themoviedb.screens.movies.mvvm.OldMoviesViewModel

/**
 * Class creating dependencies on the App level
 */
class AppComponent(appContext: Context) {

  val moviesVmFactory: MoviesVmFactory
  val oldMoviesVmFactory: OldMoviesViewModel.Factory

  init {
    val navigator = WebNavigator(appContext)

    val api = Retrofit.Builder()
      .baseUrl(BuildConfig.BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(MoviesApi::class.java)

    val moviesRepo = MoviesRepository(api)

    moviesVmFactory = MoviesVmFactory(moviesRepo, navigator)
    oldMoviesVmFactory = OldMoviesViewModel.Factory(moviesRepo, navigator)
  }
}
