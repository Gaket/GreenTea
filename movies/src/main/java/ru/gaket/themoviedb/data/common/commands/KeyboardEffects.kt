package ru.gaket.themoviedb.data.common.commands

import ru.gaket.tea.runtime.coroutines.Command
import ru.gaket.themoviedb.data.common.KeyboardManager

object KeyboardCommands {

  object HideKeyboard : Command<KeyboardManager, Unit> by Command.onMain.idle({ keyboardManager ->
    keyboardManager.hideKeyboard()
  })
}
