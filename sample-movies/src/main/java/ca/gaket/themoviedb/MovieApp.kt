package ca.gaket.themoviedb

import android.app.Application
import ca.gaket.themoviedb.di.AppComponent
import ca.gaket.themoviedb.utils.FullstoryTree
import ca.gaket.tools.logging.CrashlyticsTree
import ca.gaket.tools.logging.WtfNotifyQaTree
import com.fullstory.FSOnReadyListener
import com.fullstory.FSSessionData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import net.gaket.tools.logging.CrashlyticsTree
import net.gaket.tools.logging.FileLoggingTree
import net.gaket.tools.logging.WtfNotifyQaTree
import net.gaket.themoviedb.di.AppComponent
import net.gaket.themoviedb.utils.FullstoryTree
import timber.log.Timber

class MovieApp : Application(), FSOnReadyListener {

  // You can use whatever DI framework you are used to
  // We have some Toothpic bindings in base classes that do the injection for us
  val appComponent: AppComponent by lazy { AppComponent(this) }

  override fun onCreate() {
    super.onCreate()
    plantTimberForest()
  }

  private fun plantTimberForest() {
    Timber.plant(CrashlyticsTree())
    Timber.plant(WtfNotifyQaTree(this))
    Timber.plant(FullstoryTree())
    Timber.plant(FileLoggingTree(this))
  }

  override fun onReady(sessionData: FSSessionData) {
    FirebaseCrashlytics.getInstance().setCustomKey("FsSession", sessionData.currentSessionURL)
  }
}
