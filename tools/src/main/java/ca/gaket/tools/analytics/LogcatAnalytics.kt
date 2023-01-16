package ca.gaket.tools.analytics

import timber.log.Timber
import javax.inject.Inject

class LogcatAnalytics @Inject constructor() : AnalyticsService {

  override fun track(eventName: String, params: Map<String, Any>) {
    Timber.tag("LogcatAnalytics")
    Timber.i("$eventName: $params")
  }

  override fun screenView(screen: String, params: Map<String, Any>) {
    Timber.tag("LogcatAnalytics")
    Timber.i("screen_view: ${params.plus("screen_name" to screen)}")
  }

  override fun userProperties(properties: Map<String, String>) {
    Timber.tag("LogcatAnalytics")
    Timber.i("Super properties: $properties")
  }

  override fun identify(userId: String) {
    Timber.tag("LogcatAnalytics")
    Timber.i("Identify: $userId")
  }

  override fun resetUserId() {
    Timber.tag("LogcatAnalytics")
    Timber.i("Reset user id")
  }
}

