package ca.gaket.tea.runtime.coroutines

interface CanBeThrottled {

  fun isThrottled(last: CanBeThrottled): Boolean
}

interface CanBeThrottledByEquals : CanBeThrottled {

  override fun isThrottled(last: CanBeThrottled): Boolean = last == this
}

interface CanBeThrottledByClass : CanBeThrottled {

  override fun isThrottled(last: CanBeThrottled): Boolean = last::class == this::class
}

interface CanBeThrottledByTimestamp : CanBeThrottled {

  val createdAt: Long

  override fun isThrottled(last: CanBeThrottled): Boolean =
    if (last !is CanBeThrottledByTimestamp) false
    else this.createdAt - last.createdAt < THROTTLING_THRESHOLD

  companion object {
    const val THROTTLING_THRESHOLD = 300
  }
}
