package ca.gaket.tools.analytics

/**
 * Shows that this service is interested in some user details when a user logins
 */
interface IdentifiableService {

  fun identify(userId: String)

  fun resetUserId()
}

/**
 * Product analytics is posted through this interface
 */
interface AnalyticsService : IdentifiableService {

  fun track(eventName: String, params: Map<String, Any> = emptyMap())

  fun screenView(screen: String, params: Map<String, Any> = emptyMap())

  fun userProperties(properties: Map<String, String>)

}
