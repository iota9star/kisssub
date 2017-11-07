package star.iota.kisssub.base

import android.support.v7.widget.RecyclerView
import android.view.View


abstract class BaseViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun bindView(bean: T) {}
    open fun bindView(bean: T, callback: Callback?) {}
}
