package ru.gaket.themoviedb.ru.gaket.themoviedb.presentation.movies

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Class responsible for the navigation
 */
class Navigator(private val context: Context) {


  /**
   * Open a browser for a given url
   *
   * @return [true] if navigation succeded, [false] otherwise
   */
  fun navigateTo(url: String): Boolean {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    browserIntent.flags = browserIntent.flags or Intent.FLAG_ACTIVITY_NEW_TASK
    try {
      context.startActivity(browserIntent)
      return true
    } catch (e: ActivityNotFoundException) {
      return false
    }
  }
}