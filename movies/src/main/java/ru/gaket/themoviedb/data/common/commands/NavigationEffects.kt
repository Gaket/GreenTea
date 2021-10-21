package ru.gaket.themoviedb.data.common.commands

import ru.gaket.tea.runtime.coroutines.Command
import ru.gaket.themoviedb.data.common.Navigator
import ru.gaket.themoviedb.data.common.Screen


object NavigationCommands {

  class Forward(screen: Screen) : Command<Navigator, Unit> by Command.onMain.idle({ navigator ->
    navigator.forward(screen)
  })

  object Back : Command<Navigator, Unit> by Command.onMain.idle({ navigator ->
    navigator.back()
  })

  // Also, BackTo,
}
