package ca.gaket.tea

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect

abstract class GreenTeaFragment<State : Any, Msg : Any, Dependency : Any> : Fragment {

  private val TAG = this::class.simpleName

  protected abstract val viewModel: GreenTeaViewModel<State, Msg, Dependency>

  constructor() : super()
  constructor(@LayoutRes layoutRes: Int) : super(layoutRes)

  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initBackButtonHandler()
    initDispatchers()

    lifecycleScope.launchWhenResumed {
      viewModel.state.collect { state ->
        renderAndLog(state)
      }
    }

    viewModel.onCreated()
  }

  /**
   * Override this method to set a function to be called on a back button press.
   * A typical example is to send some kind of Feature.Message.Back message
   */
  protected open val backButtonCallback: (() -> Unit)? = null

  abstract fun initDispatchers()
  abstract fun render(state: State)

  protected fun dispatch(msg: Msg) {
    viewModel.dispatch(msg)
  }

  private fun initBackButtonHandler() {
    val currentCallback = backButtonCallback
    if (currentCallback != null) {
      val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          currentCallback.invoke()
        }
      }
      requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)
    }
  }

  private fun renderAndLog(state: State) {
    Log.v(TAG,"Rendering state: $state")
    render(state)
  }
}
