package ca.gaket.themoviedb.data.common.effects

import ca.gaket.tea.runtime.coroutines.Effect
import ca.gaket.themoviedb.data.common.KeyboardManager

object KeyboardEffects {

  object HideKeyboard : Effect<KeyboardManager, Unit> by Effect.onMain.idle({ keyboardManager ->
    keyboardManager.hideKeyboard()
  })
}
