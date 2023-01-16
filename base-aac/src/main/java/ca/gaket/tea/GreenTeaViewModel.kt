package ca.gaket.tea

import android.util.Log
import androidx.annotation.CallSuper
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.gaket.tea.runtime.coroutines.GreenTeaRuntime
import ca.gaket.tea.runtime.coroutines.Update
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class GreenTeaViewModel<State : Any, Message : Any, Dependencies : Any>(
  init: Update<State, Message, Dependencies>,
  update: (Message, State) -> Update<State, Message, Dependencies>,
  restore: (State) -> Update<State, Message, Dependencies>,
  dependencies: Dependencies,
  savedStateHandle: SavedStateHandle
) : ViewModel() {

  private val tag = this::class.simpleName
  private val savedStateHandlerKey = javaClass.name

  private val runtime by lazy {
    val result = GreenTeaRuntime(
      init = {
        val currentState = _state.value
        if (currentState == null) init
        else restore(currentState)
      },
      update = update,
      dependencies = dependencies,
      exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(tag, "Unhandled exception", throwable)
      }
    )
    result.listenState { _state.tryEmit(it) }
    result
  }

  private val _state: MutableStateFlow<State>
  val state: Flow<State>

  init {
    val initialValue = savedStateHandle.getStateFlow<State>(savedStateHandlerKey, init.state).value
    _state = MutableStateFlow(initialValue)
    state = _state
  }

  @CallSuper
  open fun onCreated() {
    runtime.ensureActive()
  }

  override fun onCleared() {
    runtime.cancel()
    super.onCleared()
  }

  fun dispatch(Message: Message) = runtime.dispatch(Message)
}
