package ru.gaket.themoviedb.screens.movies.mvvm

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import kotlinx.coroutines.launch
import ru.gaket.themoviedb.MovieApp
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.databinding.MoviesFragmentBinding
import ru.gaket.themoviedb.screens.movies.common.GridSpacingItemDecoration
import ru.gaket.themoviedb.screens.movies.common.MoviesAdapter
import ru.gaket.themoviedb.utils.afterTextChanged
import ru.gaket.themoviedb.utils.hideKeyboard


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
        viewModel.queryChannel.send("")
      }
    }
    binding.searchInput.afterTextChanged {
      lifecycleScope.launch {
        viewModel.queryChannel.send(it.toString())
      }
    }

    viewModel.searchResult.observe(viewLifecycleOwner, { handleMoviesList(it) })
    viewModel.searchState.observe(viewLifecycleOwner, { handleLoadingState(it) })
  }

  private fun handleLoadingState(it: SearchState) {
    when (it) {
      Loading -> {
        binding.searchIcon.visibility = View.GONE
        binding.searchProgress.visibility = View.VISIBLE
      }
      Ready -> {
        binding.searchIcon.visibility = View.VISIBLE
        binding.searchProgress.visibility = View.GONE
      }
    }
  }

  private fun handleMoviesList(it: MoviesResult) {
    when (it) {
      is ValidResult -> {
        binding.moviesPlaceholder.visibility = View.GONE
        binding.moviesList.visibility = View.VISIBLE
        moviesAdapter.submitList(it.result)
      }
      is ErrorResult -> {
        moviesAdapter.submitList(emptyList())
        binding.moviesPlaceholder.visibility = View.VISIBLE
        binding.moviesList.visibility = View.GONE
        binding.moviesPlaceholder.setText(R.string.search_error)
        Log.e(MoviesFragment::class.java.name, "Something went wrong.", it.e)
      }
      is EmptyResult -> {
        moviesAdapter.submitList(emptyList())
        binding.moviesPlaceholder.visibility = View.VISIBLE
        binding.moviesList.visibility = View.GONE
        binding.moviesPlaceholder.setText(R.string.empty_result)
      }
      is EmptyQuery -> {
        moviesAdapter.submitList(emptyList())
        binding.moviesPlaceholder.visibility = View.VISIBLE
        binding.moviesList.visibility = View.GONE
        binding.moviesPlaceholder.setText(R.string.movies_placeholder)
      }
      is TerminalError -> {
        // Something went terribly wrong!
        println("Our Flow terminated unexpectedly, so we're bailing!")
        Toast.makeText(
          activity,
          getString(R.string.error_unknown_on_download),
          Toast.LENGTH_SHORT
        ).show()
      }
    }
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
