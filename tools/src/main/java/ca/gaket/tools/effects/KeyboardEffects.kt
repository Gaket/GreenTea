package ca.gaket.tools.effects

import ca.gaket.tea.runtime.coroutines.Effect
import ca.gaket.tools.android.KeyboardManager

object KeyboardEffects {
  object HideKeyboard : Effect<KeyboardManager, Unit> by Effect.onMain.idle({ keyboardManager ->
    keyboardManager.hideKeyboard()
  })
}
