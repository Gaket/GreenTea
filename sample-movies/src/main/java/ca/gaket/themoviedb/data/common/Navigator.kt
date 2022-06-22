package ca.gaket.themoviedb.data.common

interface Navigator {
  fun forward(screen: Screen)
  fun back()
}
