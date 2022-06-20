package ru.gaket.themoviedb.screens.movies.mvu

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import jp.wasabeef.recyclerview.animators.FlipInBottomXAnimator
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import ru.gaket.tea.GreenTeaFragment
import ru.gaket.themoviedb.MovieApp
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.databinding.MoviesFragmentBinding
import ru.gaket.themoviedb.screens.movies.common.GridSpacingItemDecoration
import ru.gaket.themoviedb.screens.movies.common.MoviesAdapter
import ru.gaket.themoviedb.utils.afterTextChanged
import ru.gaket.themoviedb.utils.hideKeyboard
import timber.log.Timber
import java.lang.Math.random
import java.time.LocalTime


class MoviesFragment : GreenTeaFragment<MoviesFeature.State, MoviesFeature.Message, MoviesFeature.Dependencies>() {

  //region Standard stuff
  companion object {
    fun newInstance() = MoviesFragment()
  }

  private var _binding: MoviesFragmentBinding? = null
  private lateinit var moviesAdapter: MoviesAdapter
  // This property is only valid between onCreateView and onDestroyView.
  private val binding get() = _binding!!

  override val viewModel: MoviesViewModel by viewModels { getVmFactory() }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    initViews(inflater, container)
    return binding.root
  }

  override fun onResume() {
    super.onResume()
    Timber.i("${this.javaClass.name} is opened")
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  private fun getVmFactory(): MoviesVmFactory =
    (requireActivity().application as MovieApp).appComponent.moviesVmFactory

  private fun initViews(inflater: LayoutInflater, container: ViewGroup?) {
    _binding = MoviesFragmentBinding.inflate(inflater, container, false)
    binding.searchIcon.setOnClickListener {
      binding.containerButtons.updateLayoutParams {
        val diff = (0..330).random()
        height += diff
      }
    }

    binding.moviesList.apply {
      val spanCount =
        // Set span count depending on layout
        when (resources.configuration.orientation) {
          Configuration.ORIENTATION_LANDSCAPE -> 4
          else -> 2
        }
      layoutManager = GridLayoutManager(activity, spanCount)

      addItemDecoration(GridSpacingItemDecoration(spanCount, resources.getDimension(R.dimen.itemsDist).toInt(), true))
      addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
          super.onScrollStateChanged(recyclerView, newState)
          if (newState == SCROLL_STATE_DRAGGING) {
            recyclerView.hideKeyboard()
          }
        }
      })
    }
  }
  //endregion

  override fun initDispatchers() {
    binding.searchInput.afterTextChanged { query ->
      dispatch(MoviesFeature.Message.SearchUpdated(query, LocalTime.now()))
    }
    moviesAdapter = MoviesAdapter { movie ->
      dispatch(MoviesFeature.Message.MovieClicked(movie))
    }
    binding.moviesList.adapter = moviesAdapter
  }

  override fun render(state: MoviesFeature.State) {
    if (state.isLoading) {
      binding.searchIcon.visibility = View.GONE
      binding.searchProgress.visibility = View.VISIBLE
    } else {
      binding.searchIcon.visibility = View.VISIBLE
      binding.searchProgress.visibility = View.GONE
    }
    moviesAdapter.submitList(state.movies)
    binding.moviesPlaceholder.text = state.message.resolve(requireActivity())
  }

}
