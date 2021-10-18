package ru.gaket.themoviedb.utils

import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

val Int.toDp: Int
  get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.toPx: Int
  get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
  this.addTextChangedListener(object : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
      // do nothing
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
      // do nothing
    }

    override fun afterTextChanged(editable: Editable?) {
      afterTextChanged.invoke(editable.toString())
    }
  })
}

fun View.hideKeyboard() {
  val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
  imm.hideSoftInputFromWindow(windowToken, 0)
}
