package ca.gaket.themoviedb

import android.app.Application
import ca.gaket.themoviedb.di.AppComponent
import ca.gaket.tools.logging.CrashlyticsTree
import ca.gaket.tools.logging.WtfNotifyQaTree
import net.gaket.tools.logging.FileLoggingTree
import timber.log.Timber

class MovieApp : Application() {

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
    Timber.plant(FileLoggingTree(this))
  }

}
