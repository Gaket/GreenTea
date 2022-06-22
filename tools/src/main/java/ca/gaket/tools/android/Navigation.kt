package ca.gaket.tools.android

interface Screen {
  val screenKey: String get() = this::class.java.name
}

interface Router {
  fun navigateTo(screen: Screen, clearContainer: Boolean)
  fun replaceScreen(screen: Screen)
  fun newRootScreen(screen: Screen)
  fun exit()
  fun backTo(screen: Screen?)
}
