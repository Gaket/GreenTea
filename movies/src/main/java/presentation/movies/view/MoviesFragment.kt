package ru.gaket.themoviedb.presentation.movies.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import net.gaket.greentea.GreenTeaFragment
import ru.gaket.themoviedb.MovieApp
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.databinding.MoviesFragmentBinding
import ru.gaket.themoviedb.presentation.movies.utils.afterTextChanged
import ru.gaket.themoviedb.presentation.movies.utils.hideKeyboard


class MoviesFragment : GreenTeaFragment<MoviesFeature.State, MoviesFeature.Message, MoviesFeature.Dependencies>() {

  companion object {
    fun newInstance() = MoviesFragment()
  }

  private var _binding: MoviesFragmentBinding? = null
  private lateinit var moviesAdapter: MoviesAdapter

  override val viewModel: MoviesViewModel by viewModels { getVmFactory() }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = MoviesFragmentBinding.inflate(inflater, container, false)
    binding.moviesList.apply {
      val spanCount =
        // Set span count depending on layout
        when (resources.configuration.orientation) {
          Configuration.ORIENTATION_LANDSCAPE -> 4
          else -> 2
        }
      layoutManager = GridLayoutManager(activity, spanCount)
      moviesAdapter = MoviesAdapter { movie ->
        dispatch(MoviesFeature.Message.MovieClicked(movie))
      }
      adapter = moviesAdapter
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
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    if (savedInstanceState == null) {
      dispatch(MoviesFeature.Message.SearchUpdated(""))
    }
    binding.searchInput.afterTextChanged { query ->
      dispatch(MoviesFeature.Message.SearchUpdated(query))
    }
  }

  // This property is only valid between onCreateView and onDestroyView.
  private val binding get() = _binding!!

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun render(state: MoviesFeature.State) {
    if (state.loading) {
      binding.searchIcon.visibility = View.GONE
      binding.searchProgress.visibility = View.VISIBLE
    } else {
      binding.searchIcon.visibility = View.VISIBLE
      binding.searchProgress.visibility = View.GONE
    }
    moviesAdapter.submitList(state.movies)
    binding.moviesPlaceholder.text = state.message.resolve(requireActivity())
  }

  private fun getVmFactory(): MoviesVmFactory =
    (requireActivity().application as MovieApp).appComponent.moviesVmFactory

}
