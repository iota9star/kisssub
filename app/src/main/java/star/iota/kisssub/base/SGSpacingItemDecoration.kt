package star.iota.kisssub.base

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View


class SGSpacingItemDecoration(private var spanCount: Int, private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val spanIndex = (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).spanIndex
        when {
            spanCount == 1 -> {
                outRect.left = spacing
                outRect.right = spacing
            }
            spanIndex == 0 -> {
                outRect.left = spacing
                outRect.right = spacing / 2
            }
            spanIndex == spanCount - 1 -> {
                outRect.left = spacing / 2
                outRect.right = spacing
            }
            else -> {
                outRect.left = spacing / 2
                outRect.right = spacing / 2
            }
        }
        outRect.top = spacing
    }
}