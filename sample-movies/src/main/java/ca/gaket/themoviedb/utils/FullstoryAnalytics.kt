package ca.gaket.themoviedb.utils

import ca.gaket.tools.analytics.AnalyticsService
import com.fullstory.FS
import javax.inject.Inject

class FullstoryAnalytics @Inject constructor(
) : AnalyticsService {

  override fun track(eventName: String, params: Map<String, Any>) {
    FS.event(eventName, params)
  }

  override fun screenView(screen: String, params: Map<String, Any>) {
    FS.event("screen_view", params.plus("screen_name" to screen))
  }

  override fun userProperties(properties: Map<String, String>) {
    FS.setUserVars(properties)
  }

  override fun identify(userId: String) {
    FS.identify(userId)
  }

  override fun resetUserId() {
    FS.anonymize()
  }
}

