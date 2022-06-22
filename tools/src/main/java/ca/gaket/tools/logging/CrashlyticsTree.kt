package ca.gaket.tools.logging

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber


/**
 * Tree that is responsible for sending crashes and logs to Crashlytics
 */
class CrashlyticsTree : RemoteLoggerTree() {

  private val crashlytics: FirebaseCrashlytics? =
    try {
      FirebaseCrashlytics.getInstance()
    } catch (throwable: Throwable) {
      Timber.e(throwable)
      null
    }

  override fun logException(t: Throwable) {
    crashlytics?.recordException(t)
  }

  override fun logTraceRecord(taggedMessage: String) {
    crashlytics?.log(taggedMessage)
  }
}
