package ca.gaket.tools.effects

import ca.gaket.tea.runtime.coroutines.Effect
import ca.gaket.tools.analytics.AnalyticsService

object AnalyticsEffects {

  data class SendEvent<Deps, out Msg>(
    val eventName: String,
    val params: Map<String, Any> = emptyMap(),
    val analyticsProvider: Deps.() -> AnalyticsService
  ) :
    Effect<Deps, Msg> by Effect.idle({ deps ->
      deps.analyticsProvider().track(eventName, params)
    })
}
