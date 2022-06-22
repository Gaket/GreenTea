package ca.gaket.tools.analytics

/**
 * This class implements Composite pattern, allowing to log data into multiple
 * analytics services at once, working as a single entry point.
 */
class AnalyticsAggregator(
  private val analyticsServices: List<AnalyticsService>,
  private val identifiableServices: List<IdentifiableService>,
) : AnalyticsService {

  override fun track(eventName: String, params: Map<String, Any>) {
    analyticsServices.forEach { it.track(eventName, params) }
  }

  override fun screenView(screen: String, params: Map<String, Any>) {
    analyticsServices.forEach { it.screenView(screen, params) }
  }

  override fun userProperties(properties: Map<String, String>) {
    analyticsServices.forEach { it.userProperties(properties) }
  }

  override fun identify(userId: String) {
    analyticsServices.forEach { it.identify(userId) }
    identifiableServices.forEach { it.identify(userId) }
  }

  override fun resetUserId() {
    analyticsServices.forEach { it.resetUserId() }
    identifiableServices.forEach { it.resetUserId() }
  }
}
