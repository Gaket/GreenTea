package ca.gaket.themoviedb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ca.gaket.themoviedb.screens.movies.mvvm.MoviesFragment

class MvvmActivity : AppCompatActivity() {

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
