package ca.gaket.tools.effects

import ca.gaket.tea.runtime.coroutines.Effect
import ca.gaket.tools.android.Router
import ca.gaket.tools.android.Screen

object NavigationEffects {

  class Replace<Dependencies, out Message>(val screen: Screen, routerProvider: Dependencies.() -> Router) :
    Effect<Dependencies, Message> by Effect.onMain.idle({ dependencies ->
      dependencies.routerProvider().replaceScreen(screen)
    })

  class NewRoot<Dependencies, out Message>(val screen: Screen, routerProvider: Dependencies.() -> Router) :
    Effect<Dependencies, Message> by Effect.onMain.idle({ dependencies ->
      dependencies.routerProvider().newRootScreen(screen)
    })

  class NavigateTo<Dependencies, out Message>(
    val screen: Screen,
    val clearContainer: Boolean = true,
    routerProvider: Dependencies.() -> Router
  ) :
    Effect<Dependencies, Message> by Effect.onMain.idle({ dependencies ->
      dependencies.routerProvider().navigateTo(screen, clearContainer)
    })

  class Exit<Dependencies, out Message>(routerProvider: Dependencies.() -> Router) :
    Effect<Dependencies, Message> by Effect.onMain.idle({ dependencies ->
      dependencies.routerProvider().exit()
    })


  class BackTo<Dependencies, out Message>(val screen: Screen, routerProvider: Dependencies.() -> Router) :
    Effect<Dependencies, Message> by Effect.onMain.idle({ dependencies ->
      dependencies.routerProvider().backTo(screen)
    })

  class BackToRoot<Dependencies, out Message>(routerProvider: Dependencies.() -> Router) :
    Effect<Dependencies, Message> by Effect.onMain.idle({ dependencies ->
      dependencies.routerProvider().backTo(null)
    })
}
