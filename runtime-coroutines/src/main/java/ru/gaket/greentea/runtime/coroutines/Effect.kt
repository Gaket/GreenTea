package ru.gaket.tea.runtime.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

typealias Action<Dependencies, Message> = suspend CoroutineScope.(Dependencies) -> Flow<Message>?

interface Command<Dependencies, out Message> {
  val run: Action<Dependencies, Message>

  companion object {

    operator fun <Dependencies, Message> invoke(action: Action<Dependencies, Message>) = object : Command<Dependencies, Message> {
      override val run = action
    }

    fun <Dependencies, Message> single(f: suspend CoroutineScope.(Dependencies) -> Message) = Builder().single(f)
    fun <Dependencies, Message> idle(f: suspend CoroutineScope.(Dependencies) -> Unit) = Builder().idle<Dependencies, Message>(f)
    fun <Dependencies, Message> flow(f: suspend CoroutineScope.(Dependencies) -> Flow<Message>) = Builder().flow(f)

    val onMain get() = Builder(Dispatchers.Main)
    val onIO get() = Builder(Dispatchers.IO)
    val onDefault get() = Builder(Dispatchers.Default)
    val onUnconfined get() = Builder(Dispatchers.Unconfined)

    class Builder(
      private val dispatcher: CoroutineDispatcher? = null
    ) {
      fun <Dependencies, Message> single(f: suspend CoroutineScope.(Dependencies) -> Message) =
        withDispatcher<Dependencies, Message> { flowOf(f(it)) }

      fun <Dependencies, Message> idle(f: suspend CoroutineScope.(Dependencies) -> Unit) =
        withDispatcher<Dependencies, Message> { f(it); null }

      fun <Dependencies, Message> flow(f: suspend CoroutineScope.(Dependencies) -> Flow<Message>) =
        withDispatcher(f)

      private fun <Dependencies, Message> withDispatcher(
        action: Action<Dependencies, Message>
      ) = Command<Dependencies, Message> {
        if (dispatcher != null) withContext(dispatcher) { action(it) }
        else action(it)
      }
    }
  }
}

inline fun <Dependencies1, Message, Dependencies2, Message2> Command<Dependencies1, Message>.adapt(
  crossinline f1: (Flow<Message>?) -> Flow<Message2>?,
  crossinline f2: (Dependencies2) -> Dependencies1
) = Command<Dependencies2, Message2> { d2 ->
  val d = f2(d2)
  val message = this@adapt.run(this, d)
  f1(message)
}

inline fun <Dependencies1, Message, Dependencies2, Message2> Command<Dependencies1, Message>.adaptIdle(
  crossinline fa: (Dependencies2) -> Dependencies1
): Command<Dependencies2, Message2> = adapt({ null }, fa)
