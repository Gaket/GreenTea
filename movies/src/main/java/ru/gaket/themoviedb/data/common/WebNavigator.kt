package ru.gaket.themoviedb.data.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Class responsible for the navigation
 */
class WebNavigator(private val context: Context) : Navigator {

  /**
   * Open a browser for a given url
   *
   * @return [true] if navigation succeeded, [false] otherwise
   */
  fun navigateTo(url: String): Boolean {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    browserIntent.flags = browserIntent.flags or Intent.FLAG_ACTIVITY_NEW_TASK
    return try {
      context.startActivity(browserIntent)
      true
    } catch (e: ActivityNotFoundException) {
      false
    }
  }

  override fun forward(screen: Screen) {
    if (screen is Screen.MovieDetails) {
      navigateTo("https://www.themoviedb.org/movie/${screen.movieId}")
    } else {
      throw NotImplementedError("Sorry, that's an abstraction leak. Operation not supported")
    }
  }

  override fun back() {
    throw NotImplementedError("Sorry, that's an abstraction leak. Operation not supported")
  }
}
