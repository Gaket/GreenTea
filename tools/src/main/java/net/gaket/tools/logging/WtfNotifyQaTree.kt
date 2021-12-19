package net.gaket.tools.logging

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import timber.log.Timber


/**
 * A tree that alerts users in non-release builds about an Assert-level log via a long Toast message
 */
class WtfNotifyQaTree(private val context: Application) : Timber.Tree() {

  override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
    if (priority != Log.ASSERT) {
      return
    }

    Handler(Looper.getMainLooper()).post {
      Toast.makeText(context, "Internal Error! $message", Toast.LENGTH_SHORT).show()
      Toast.makeText(context, "Internal Error! $message", Toast.LENGTH_LONG).show()
    }
  }
}
