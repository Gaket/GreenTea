package com.getsquire.common.presentation.effects

import ca.gaket.tea.runtime.coroutines.Effect
import ca.gaket.tools.android.Router
import ca.gaket.tools.android.Screen

object NavigationEffects {

  class Replace<Deps, out Msg>(val screen: Screen, routerProvider: Deps.() -> Router) :
    Effect<Deps, Msg> by Effect.onMain.idle({ deps ->
      deps.routerProvider().replaceScreen(screen)
    })

  class NewRoot<Deps, out Msg>(val screen: Screen, routerProvider: Deps.() -> Router) :
    Effect<Deps, Msg> by Effect.onMain.idle({ deps ->
      deps.routerProvider().newRootScreen(screen)
    })

  class NavigateTo<Deps, out Msg>(
    val screen: Screen,
    val clearContainer: Boolean = true,
    routerProvider: Deps.() -> Router
  ) :
    Effect<Deps, Msg> by Effect.onMain.idle({ deps ->
      deps.routerProvider().navigateTo(screen, clearContainer)
    })

  class Exit<Deps, out Msg>(routerProvider: Deps.() -> Router) :
    Effect<Deps, Msg> by Effect.onMain.idle({ deps ->
      deps.routerProvider().exit()
    })


  class BackTo<Deps, out Msg>(val screen: Screen, routerProvider: Deps.() -> Router) :
    Effect<Deps, Msg> by Effect.onMain.idle({ deps ->
      deps.routerProvider().backTo(screen)
    })

  class BackToRoot<Deps, out Msg>(routerProvider: Deps.() -> Router) :
    Effect<Deps, Msg> by Effect.onMain.idle({ deps ->
      deps.routerProvider().backTo(null)
    })
}
