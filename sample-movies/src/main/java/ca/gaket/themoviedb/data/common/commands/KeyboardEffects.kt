package ca.gaket.themoviedb.data.common.commands

import ca.gaket.tea.runtime.coroutines.Effect
import ca.gaket.themoviedb.data.common.KeyboardManager

object KeyboardCommands {

  object HideKeyboard : Effect<KeyboardManager, Unit> by Effect.onMain.idle({ keyboardManager ->
    keyboardManager.hideKeyboard()
  })
}
