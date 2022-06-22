package ca.gaket.themoviedb.screens.movies.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * Decorator adding spaces between elements in a grid
 */
class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) : ItemDecoration() {
  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    val position = parent.getChildAdapterPosition(view)
    val column = position % spanCount
    if (includeEdge) {
      outRect.left = spacing - column * spacing / spanCount
      outRect.right = (column + 1) * spacing / spanCount
      outRect.bottom = spacing / 2
    } else {
      outRect.left = column * spacing / spanCount
      outRect.right = spacing - (column + 1) * spacing / spanCount
    }
  }

}
