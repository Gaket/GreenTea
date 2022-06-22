package ca.gaket.themoviedb.data.common.commands

import ca.gaket.tea.runtime.coroutines.Effect
import ca.gaket.themoviedb.data.common.Navigator
import ca.gaket.themoviedb.data.common.Screen


object NavigationCommands {

  class Forward(screen: Screen) : Effect<Navigator, Unit> by Effect.onMain.idle({ navigator ->
    navigator.forward(screen)
  })

  object Back : Effect<Navigator, Unit> by Effect.onMain.idle({ navigator ->
    navigator.back()
  })

  // Also, BackTo,
}
