package ca.gaket.themoviedb.di

import android.content.Context
import ca.gaket.themoviedb.BuildConfig
import ca.gaket.themoviedb.data.common.WebNavigator
import ca.gaket.themoviedb.data.network.MoviesApi
import ca.gaket.themoviedb.data.repositories.MoviesRepository
import ca.gaket.themoviedb.screens.movies.mvu.MoviesVmFactory
import ca.gaket.themoviedb.screens.movies.mvvm.OldMoviesViewModel
import ca.gaket.tools.analytics.AnalyticsAggregator
import ca.gaket.tools.interceptors.ErrorLoggingInterceptor
import ca.gaket.tools.interceptors.ErrorToastInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

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
      .client(provideOkHttpClient(appContext))
      .build()
      .create(MoviesApi::class.java)

    val moviesRepo = MoviesRepository(api)
    val analyticsService = AnalyticsAggregator(emptyList(), emptyList())
    moviesVmFactory = MoviesVmFactory(moviesRepo, navigator, analyticsService)
    oldMoviesVmFactory = OldMoviesViewModel.Factory(moviesRepo, navigator)
  }

  private fun provideOkHttpClient(appContext: Context): OkHttpClient {
    val builder = OkHttpClient.Builder()
      .readTimeout(1, TimeUnit.MINUTES)
      .writeTimeout(1, TimeUnit.SECONDS)
      .connectTimeout(20, TimeUnit.SECONDS)

    if (BuildConfig.DEBUG) {
      builder.addNetworkInterceptor(ErrorToastInterceptor(appContext))
    }

    builder.addNetworkInterceptor(
      ErrorLoggingInterceptor(
        mainLogger = object : ErrorLoggingInterceptor.Logger {
          override fun log(message: String) {
            Timber.v(message)
          }
        },
        httpErrorsLogger = object : ErrorLoggingInterceptor.Logger {
          override fun log(message: String) {
            Timber.w(message)
          }
        })
    )
    return builder.build()
  }
}
