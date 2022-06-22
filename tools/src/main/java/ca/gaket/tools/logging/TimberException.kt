package ca.gaket.tools.logging

import timber.log.Timber
import java.util.Arrays

/**
 * Throwable that clears all the Timber traces from StackTrace
 * Useful for Crashlytics or other systems error logging not to have all errors
 * inside "CrashlyticsTree" event
 */
open class TimberException internal constructor(detailMessage: String?) : Exception(detailMessage) {

  override fun fillInStackTrace(): Throwable {
    super.fillInStackTrace()
    val unmodifiableStack = Arrays.asList(*stackTrace)
    var stackTraceElements: MutableList<StackTraceElement> = ArrayList(unmodifiableStack)
    val traceElementIterator = stackTraceElements.iterator()

    // Remove all the top stacktrace elements starting from Timber call
    while (traceElementIterator.hasNext()) {
      val stackTraceElement = traceElementIterator.next()
      traceElementIterator.remove()
      if (isTimberRoot(stackTraceElement)) {
        break
      }
    }

    // Just in case if something goes wrong, set the full stacktrace
    if (stackTraceElements.isEmpty()) {
      stackTraceElements = unmodifiableStack
    }
    stackTrace = stackTraceElements.toTypedArray()
    return this
  }

  private fun isTimberRoot(stackTraceElement: StackTraceElement): Boolean {
    return stackTraceElement.className == Timber::class.java.name
  }
}
