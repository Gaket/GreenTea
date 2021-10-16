package ru.gaket.themoviedb

import android.app.Application
import ru.gaket.themoviedb.di.AppComponent

class MovieApp : Application() {

  val myComponent: AppComponent by lazy { AppComponent(this) }
}