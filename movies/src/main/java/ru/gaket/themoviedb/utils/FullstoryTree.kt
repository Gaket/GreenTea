package ru.gaket.themoviedb.utils

import com.fullstory.FS
import net.gaket.tools.RemoteLoggerTree


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
