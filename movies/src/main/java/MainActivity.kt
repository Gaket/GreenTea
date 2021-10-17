package ru.gaket.themoviedb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.gaket.themoviedb.presentation.movies.view.MoviesFragment

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_activity)
    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
          .replace(R.id.container, MoviesFragment.newInstance())
          .commitNow()
    }
  }
}
