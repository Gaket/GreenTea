package ca.gaket.tools.effects

import ca.gaket.tea.runtime.coroutines.Effect
import ca.gaket.tools.analytics.AnalyticsService

object AnalyticsEffects {

  data class SendEvent<Dependencies, out Message>(
    val eventName: String,
    val params: Map<String, Any> = emptyMap(),
    val analyticsProvider: Dependencies.() -> AnalyticsService
  ) :
    Effect<Dependencies, Message> by Effect.idle({ dependencies ->
      dependencies.analyticsProvider().track(eventName, params)
    })
}
