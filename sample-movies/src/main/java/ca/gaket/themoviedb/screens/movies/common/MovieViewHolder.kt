package ca.gaket.themoviedb.screens.movies.common

import androidx.recyclerview.widget.RecyclerView
import ca.gaket.themoviedb.R
import ca.gaket.themoviedb.data.entities.Movie
import ca.gaket.themoviedb.databinding.ItemMovieBinding
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation


class MovieViewHolder(private val binding: ItemMovieBinding) :
    RecyclerView.ViewHolder(binding.root) {

  private val transformation: Transformation

  init {
    val dimension = itemView.resources.getDimension(R.dimen.cornerRad)
    val cornerRadius = dimension.toInt()
    transformation = RoundedCornersTransformation(cornerRadius, 0)
  }

  fun bind(movie: Movie, listener: (Movie) -> Unit) {
    setName(movie)
    setThumbnail(movie)
    setClickListener(listener, movie)
  }

  private fun setClickListener(
    listener: (Movie) -> Unit,
    movie: Movie
  ) {
    itemView.setOnClickListener { listener(movie) }
  }

  private fun setName(movie: Movie) {
    binding.movieName.text = movie.name
  }

  private fun setThumbnail(movie: Movie) {
    Picasso.get()
        .load(movie.thumbnail)
        .placeholder(R.drawable.ph_movie_grey_200)
        .error(R.drawable.ph_movie_grey_200)
        .transform(transformation)
        .fit()
        .centerCrop()
        .into(binding.movieThumbnail)
  }
}
