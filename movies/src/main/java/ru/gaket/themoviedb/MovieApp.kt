package ru.gaket.themoviedb

import android.app.Application
import net.gaket.tools.logging.CrashlyticsTree
import net.gaket.tools.logging.WtfNotifyQaTree
import ru.gaket.themoviedb.di.AppComponent
import ru.gaket.themoviedb.utils.FullstoryTree
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
    Timber.plant(Timber.DebugTree())
    Timber.plant(CrashlyticsTree())
    Timber.plant(WtfNotifyQaTree(this))
    Timber.plant(FullstoryTree())
  }
}
