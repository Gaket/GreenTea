package ru.gaket.themoviedb.screens.movies.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.gaket.themoviedb.databinding.ItemMovieBinding
import ru.gaket.themoviedb.model.entities.Movie

class MoviesAdapter(private val listener: (Movie) -> Unit) :
    ListAdapter<Movie, MovieViewHolder>(DIFF_CALLBACK) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding = ItemMovieBinding.inflate(layoutInflater, parent, false)
    return MovieViewHolder(binding)
  }

  override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
    holder.bind(getItem(position), listener)
  }

  companion object {
    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
      override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
          oldItem.id == newItem.id

      override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
          oldItem == newItem
    }
  }
}
