package ca.gaket.themoviedb.utils

import ca.gaket.tools.logging.RemoteLoggerTree
import com.fullstory.FS


/**
 * Tree that is responsible for sending crashes and logs to Fullstory.
 * This tree sends log data straight to the Fullstory's log view
 */
class FullstoryTree : RemoteLoggerTree()  {

  override fun logTraceRecord(taggedMessage: String) {
    FS.log(FS.LogLevel.INFO, taggedMessage)
  }

  override fun logException(t: Throwable) {
    FS.log(FS.LogLevel.ERROR, t.toString())
  }

}
