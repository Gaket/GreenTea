package ca.gaket.themoviedb.screens.movies.mvvm

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import ca.gaket.themoviedb.MovieApp
import ca.gaket.themoviedb.R
import ca.gaket.themoviedb.databinding.MoviesFragmentBinding
import ca.gaket.themoviedb.screens.movies.common.GridSpacingItemDecoration
import ca.gaket.themoviedb.screens.movies.common.MoviesAdapter
import ca.gaket.themoviedb.utils.afterTextChanged
import ca.gaket.themoviedb.utils.hideKeyboard
import kotlinx.coroutines.launch


class MoviesFragment : Fragment() {

  companion object {
    fun newInstance() = MoviesFragment()
  }

  private var _binding: MoviesFragmentBinding? = null
  private lateinit var moviesAdapter: MoviesAdapter

  private val viewModel: OldMoviesViewModel by viewModels { getVmFactory() }


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
      moviesAdapter = MoviesAdapter {
        viewModel.onMovieAction(it)
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

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)


    if (savedInstanceState == null) {
      lifecycleScope.launch {
        viewModel.onNewQuery("")
      }
    }
    binding.searchInput.afterTextChanged { query ->
      lifecycleScope.launch {
        viewModel.onNewQuery(query)
      }
    }

    viewModel.searchResult.observe(viewLifecycleOwner, { handleMoviesList(it) })
  }

  private fun handleMoviesList(it: MoviesResult) {
    when (it) {
      is MoviesResult.SuccessResult -> {
        hideLoading()
        binding.moviesPlaceholder.visibility = View.GONE
        binding.moviesList.visibility = View.VISIBLE
        moviesAdapter.submitList(it.result)
      }
      is MoviesResult.ErrorResult -> {
        hideLoading()
        moviesAdapter.submitList(emptyList())
        binding.moviesPlaceholder.visibility = View.VISIBLE
        binding.moviesList.visibility = View.GONE
        binding.moviesPlaceholder.setText(R.string.search_error)
        Log.e(MoviesFragment::class.java.name, "Something went wrong.", it.e)
      }
      is MoviesResult.EmptyResult -> {
        hideLoading()
        moviesAdapter.submitList(emptyList())
        binding.moviesPlaceholder.visibility = View.VISIBLE
        binding.moviesList.visibility = View.GONE
        binding.moviesPlaceholder.setText(R.string.empty_result)
      }
      is MoviesResult.EmptyQuery -> {
        hideLoading()
        moviesAdapter.submitList(emptyList())
        binding.moviesPlaceholder.visibility = View.VISIBLE
        binding.moviesList.visibility = View.GONE
        binding.moviesPlaceholder.setText(R.string.movies_placeholder)
      }
      is MoviesResult.Loading -> showLoading()
    }
  }

  private fun showLoading() {
    binding.searchIcon.visibility = View.GONE
    binding.searchProgress.visibility = View.VISIBLE
  }

  private fun hideLoading() {
    binding.searchIcon.visibility = View.VISIBLE
    binding.searchProgress.visibility = View.GONE
  }

  // This property is only valid between onCreateView and onDestroyView.
  private val binding get() = _binding!!

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  private fun getVmFactory(): OldMoviesViewModel.Factory {
    return (requireActivity().application as MovieApp).appComponent.oldMoviesVmFactory
  }

}
