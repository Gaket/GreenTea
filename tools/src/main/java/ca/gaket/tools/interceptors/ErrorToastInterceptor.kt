package ca.gaket.tools.interceptors

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

/**
 * Interceptor that shows a toast with an error body for every 400+ status code response
 */
class ErrorToastInterceptor @Inject constructor(
  val context: Context
) : Interceptor {

  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val response = chain.proceed(request)
    val peekBody = if (response.body != null) {
      response.peekBody(1024 * 1024 * 1024)
    } else null
    if (response.code > 400) {
      val responseBodyString = peekBody?.string()
      Handler(Looper.getMainLooper()).post {
        Toast.makeText(
          context,
          "Network error: ${response.code}: ${response.message}. ${responseBodyString?.take(100)}",
          LENGTH_LONG
        ).show()
      }
    }
    return response
  }
}

