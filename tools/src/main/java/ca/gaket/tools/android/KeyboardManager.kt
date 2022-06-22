package ca.gaket.tools.android

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

interface KeyboardManager {
  fun hideKeyboard()
}

fun Context.hideSoftKeyboard(view: View): Boolean {
  val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
  return imm.hideSoftInputFromWindow(view.windowToken, 0)
}
