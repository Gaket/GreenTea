package ca.gaket.tools.analytics

import com.google.firebase.crashlytics.FirebaseCrashlytics

class CrashlyticsIdentifier(
  private val crashlytics: FirebaseCrashlytics,
) : IdentifiableService {

  override fun identify(userId: String) {
    crashlytics.setUserId(userId)
  }

  // https://firebase.google.com/docs/crashlytics/customize-crash-reports?platform=ios#set-user-ids
  override fun resetUserId() {
    crashlytics.setUserId("")
  }
}
