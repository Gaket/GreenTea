package ca.gaket.tea.runtime.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class GreenTeaRuntime<State : Any, Message : Any, Dependencies : Any>(
  init: () -> Update<State, Message, Dependencies>,
  private val update: (Message, State) -> Update<State, Message, Dependencies>,
  private val dependencies: Dependencies,
  private val runtimeContext: CoroutineContext = Dispatchers.Main,
  private val renderContext: CoroutineContext = Dispatchers.Main,
  private val effectContext: CoroutineContext = Dispatchers.IO,
  exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable -> throw throwable }
) : CoroutineScope {

  override val coroutineContext: CoroutineContext = runtimeContext + SupervisorJob() + exceptionHandler

  private val stateListeners = mutableListOf<((State) -> Unit)>()

  private var lastMsg: Message? = null

  private var currentState: State

  init {
    val initial = init()
    currentState = initial.state
    step(initial, true)
  }

  fun listenState(listener: (State) -> Unit) {
    stateListeners.add(listener)
    listener(currentState)
  }

  fun dispatch(message: Message) {
    if (isActive) {
      launch(runtimeContext) {
        if (isMsgThrottled(message, lastMsg)) return@launch
        lastMsg = message
        val next = update(message, currentState)
        step(next, currentState !== next.state)
      }
    }
  }

  private fun step(next: Update<State, Message, Dependencies>, notifyStateListeners: Boolean) {
    val newState = next.state
    if (notifyStateListeners) {
      currentState = newState

      launch(renderContext) {
        stateListeners.notifyAll(newState)
      }
    }

    next.effects.forEach { effect ->
      launch(effectContext) {
        effect.run(this, dependencies)?.collect {
          dispatch(it)
        }
      }
    }
  }

  private fun <T> List<(T) -> Unit>.notifyAll(value: T) = forEach { listener -> listener.invoke(value) }

  private fun isMsgThrottled(newMessage: Message, lastMessage: Message?): Boolean {
    if (newMessage !is CanBeThrottled || lastMessage !is CanBeThrottled) return false
    return newMessage.isThrottled(lastMessage)
  }
}
