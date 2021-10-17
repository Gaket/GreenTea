package ru.gaket.themoviedb.model.common

interface Navigator {
  fun forward(screen: Screen)
  fun back()
}
