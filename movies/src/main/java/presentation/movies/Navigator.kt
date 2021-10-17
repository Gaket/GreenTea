package ru.gaket.themoviedb.presentation.movies

interface Navigator {
  fun forward(screen: Screen)
  fun back()
}
