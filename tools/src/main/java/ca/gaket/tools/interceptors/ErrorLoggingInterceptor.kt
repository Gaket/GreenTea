/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.gaket.tools.interceptors

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okhttp3.internal.platform.Platform
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

/**
 * Interceptor that logs http requests into two different loggers, depending on the response status code.
 * Everything with a code above 400 gets logged into [httpErrorsLogger]
 */
class ErrorLoggingInterceptor constructor(
    private var mainLogger: Logger = Logger.DEFAULT,
    private var httpErrorsLogger: Logger = Logger.DEFAULT
) : Interceptor {


  @Volatile
    /**
     * If you don't want a particular header to be logged, just add it here and we'll cut it
     */
  var headersToRedact = emptySet<String>()

  interface Logger {
    fun log(message: String)

    companion object {
      /** A [Logger] defaults output appropriate for the current platform. */
      @JvmField
      val DEFAULT: Logger = object :
          Logger {
        override fun log(message: String) {
          Platform.get().log(message)
        }
      }
    }
  }

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val logHeaders = true

    val startNs = System.nanoTime()
    val response: Response
    try {
      response = chain.proceed(request)
    } catch (e: Exception) {
      httpErrorsLogger.log("<-- HTTP FAILED: $e")
      throw e
    }

    val stringBuilder = StringBuilder()
    val requestBody = request.body

    val connection = chain.connection()
    var requestStartMessage =
      ("--> ${request.method} ${request.url}${if (connection != null) " " + connection.protocol() else ""}")
    if (!logHeaders && requestBody != null) {
      requestStartMessage += " (${requestBody.contentLength()}-byte body)"
    }
    stringBuilder.appendLine(requestStartMessage)

    val requestHeaders = request.headers

    if (requestBody != null) {
      // Request body headers are only present when installed as a network interceptor. When not
      // already present, force them to be included (if available) so their values are known.
      requestBody.contentType()?.let {
        if (requestHeaders["Content-Type"] == null) {
          stringBuilder.appendLine("Content-Type: $it")
        }
      }
      if (requestBody.contentLength() != -1L) {
        if (requestHeaders["Content-Length"] == null) {
          stringBuilder.appendLine("Content-Length: ${requestBody.contentLength()}")
        }
      }
    }

    for (i in 0 until requestHeaders.size) {
      logHeader(requestHeaders, i, stringBuilder)
    }

    logBody(requestBody, request, stringBuilder)

    val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

    val responseBody = response.body!!
    val contentLength = responseBody.contentLength()
    val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
    stringBuilder.appendLine(
      "<-- ${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} (${tookMs}ms${if (!logHeaders) ", $bodySize body" else ""})"
    )

    if (logHeaders) {
      val responseHeaders = response.headers
      for (i in 0 until responseHeaders.size) {
        logHeader(responseHeaders, i, stringBuilder)
      }

      if (!response.promisesBody()) {
        stringBuilder.appendLine("<-- END HTTP")
      } else if (bodyHasUnknownEncoding(response.headers)) {
        stringBuilder.appendLine("<-- END HTTP (encoded body omitted)")
      } else {
        val source = responseBody.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        var buffer = source.buffer

        var gzippedLength: Long? = null
        if ("gzip".equals(responseHeaders["Content-Encoding"], ignoreCase = true)) {
          gzippedLength = buffer.size
          GzipSource(buffer.clone()).use { gzippedResponseBody ->
            buffer = Buffer()
            buffer.writeAll(gzippedResponseBody)
          }
        }

        val contentType = responseBody.contentType()
        val charset: Charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

        if (!buffer.isProbablyUtf8()) {
          stringBuilder.appendLine("")
          stringBuilder.appendLine("<-- END HTTP (binary ${buffer.size}-byte body omitted)")
          return response
        }

        if (contentLength != 0L) {
          stringBuilder.appendLine("")
          stringBuilder.appendLine(buffer.clone().readString(charset))
        }

        if (gzippedLength != null) {
          stringBuilder.appendLine("<-- END HTTP (${buffer.size}-byte, $gzippedLength-gzipped-byte body)")
        } else {
          stringBuilder.appendLine("<-- END HTTP (${buffer.size}-byte body)")
        }
      }
    }

    val logger = if (response.code > 400) httpErrorsLogger else mainLogger
    logger.log(stringBuilder.toString())
    return response
  }

  private fun logBody(requestBody: RequestBody?, request: Request, stringBuilder: StringBuilder) {
    if (requestBody == null) {
      stringBuilder.appendLine("--> END ${request.method}")
    } else if (bodyHasUnknownEncoding(request.headers)) {
      stringBuilder.appendLine("--> END ${request.method} (encoded body omitted)")
    } else if (requestBody.isDuplex()) {
      stringBuilder.appendLine("--> END ${request.method} (duplex request body omitted)")
    } else if (requestBody.isOneShot()) {
      stringBuilder.appendLine("--> END ${request.method} (one-shot body omitted)")
    } else {
      val buffer = Buffer()
      requestBody.writeTo(buffer)

      val contentType = requestBody.contentType()
      val charset: Charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

      stringBuilder.appendLine("")
      if (buffer.isProbablyUtf8()) {
        stringBuilder.appendLine(buffer.readString(charset))
        stringBuilder.appendLine("--> END ${request.method} (${requestBody.contentLength()}-byte body)")
      } else {
        stringBuilder.appendLine(
          "--> END ${request.method} (binary ${requestBody.contentLength()}-byte body omitted)"
        )
      }
    }
  }

  private fun logHeader(headers: Headers, i: Int, stringBuilder: StringBuilder) {
    val value = if (headers.name(i) in headersToRedact) "██" else headers.value(i)
    stringBuilder.appendLine(headers.name(i) + ": " + value)
  }

  private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
    val contentEncoding = headers["Content-Encoding"] ?: return false
    return !contentEncoding.equals("identity", ignoreCase = true) &&
      !contentEncoding.equals("gzip", ignoreCase = true)
  }

  /**
   * Returns true if the body in question probably contains human readable text. Uses a small
   * sample of code points to detect unicode control characters commonly used in binary file
   * signatures.
   */
  private fun Buffer.isProbablyUtf8(): Boolean {
    try {
      val prefix = Buffer()
      val byteCount = size.coerceAtMost(64)
      copyTo(prefix, 0, byteCount)
      for (i in 0 until 16) {
        if (prefix.exhausted()) {
          break
        }
        val codePoint = prefix.readUtf8CodePoint()
        if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
          return false
        }
      }
      return true
    } catch (_: EOFException) {
      return false // Truncated UTF-8 sequence.
    }
  }
}

