package ca.gaket.tools.logging

import android.util.Log
import timber.log.Timber

/**
 * Abstract class splitting one log call into two parts depending on the log level.
 * The first part is a message that should be logged as a breadcrumb.
 * The second part is a non-fatal issue that should be logged as an error.
 */
abstract class RemoteLoggerTree : Timber.Tree() {

  companion object {
    // The tag should not exceed 23 characters: https://stackoverflow.com/a/28168739/3675659
    const val TAG_SKIP_REMOTE_LOGGER = "UncaughtExceptionHandle"
  }

  override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
    if (priority == Log.VERBOSE || priority == Log.DEBUG || tag == TAG_SKIP_REMOTE_LOGGER) {
      return
    }
    val taggedMessage = "${tag ?: "Tag"}: $message"
    if (t == null && priority != Log.ERROR) {
      logTraceRecord(taggedMessage)
    } else if (t == null && priority == Log.ERROR) {
      logTraceRecord(taggedMessage)
      logException(RuntimeException(taggedMessage))
    } else {
      logTraceRecord(taggedMessage)
      logException(t!!)
    }
  }

  protected abstract fun logTraceRecord(taggedMessage: String)

  protected abstract fun logException(t: Throwable)
}
