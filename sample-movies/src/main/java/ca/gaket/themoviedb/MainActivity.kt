package ca.gaket.themoviedb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import ca.gaket.themoviedb.screens.movies.mvu.MoviesFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_activity)
    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
        .replace(R.id.container, MoviesFragment.newInstance())
        .commitNow()
    }

    FirebaseCrashlytics.getInstance().setUserId("abc123")
    Timber.d("Let's see ho debug message works")
    Timber.i("Info is here")
    Timber.w("Oops, here goes a warning!")
    Timber.e("Test message, something wrong!")
  }

}
