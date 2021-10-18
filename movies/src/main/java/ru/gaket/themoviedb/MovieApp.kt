package ru.gaket.themoviedb

import android.app.Application
import ru.gaket.themoviedb.di.AppComponent

class MovieApp : Application() {

  // You can use whatever DI framework you are used to
  // We have some Toothpic bindings in base classes that do the injection for us
  val appComponent: AppComponent by lazy { AppComponent(this) }
}
