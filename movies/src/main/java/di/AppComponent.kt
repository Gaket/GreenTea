package ru.gaket.themoviedb.di

import android.content.Context
import presentation.movies.WebNavigator
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.gaket.themoviedb.BuildConfig
import ru.gaket.themoviedb.model.movies.network.MoviesApi
import ru.gaket.themoviedb.model.movies.repositories.MoviesRepository
import ru.gaket.themoviedb.presentation.movies.view.MoviesVmFactory

/**
 * Class creating dependencies on the App level
 */
class AppComponent(appContext: Context) {

  val moviesVmFactory: MoviesVmFactory

  init {
    val navigator = WebNavigator(appContext)

    val api = Retrofit.Builder()
      .baseUrl(BuildConfig.BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(MoviesApi::class.java)

    val moviesRepo = MoviesRepository(api)

    moviesVmFactory = MoviesVmFactory(moviesRepo, navigator)
  }
}
