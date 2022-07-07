package ca.gaket.tools.android

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.drawToBitmap
import timber.log.Timber


fun View.toBitmap(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? =
  try {
    when {
      ViewCompat.isLaidOut(this) && width > 0 && height > 0 -> {
        drawToBitmap(config)
      }
      measuredHeight <= 0 -> {
        val specWidth: Int = View.MeasureSpec.makeMeasureSpec((parent as ViewGroup).width, View.MeasureSpec.AT_MOST)
        measure(specWidth, specWidth)
        val b = Bitmap.createBitmap(measuredWidth, measuredHeight, config)
        val c = Canvas(b)
        layout(0, 0, measuredWidth, measuredHeight)
        draw(c)
        b
      }
      else -> {
        val b = Bitmap.createBitmap(measuredWidth, measuredHeight, config)
        val c = Canvas(b)
        measure(
          View.MeasureSpec.makeMeasureSpec(measuredWidth, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(measuredHeight, View.MeasureSpec.EXACTLY),
        )
        layout(this.left, this.top, this.right, this.bottom)
        draw(c)
        b
      }
    }
  } catch (e: Throwable) {
    System.gc() // we would like to free up some memory...
    Timber.e(e)
    null
  }
