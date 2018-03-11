package star.iota.kisssub.base

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

abstract class BaseAdapter<E> : RecyclerView.Adapter<BaseViewHolder<E>>() {
    private val list: ArrayList<E> by lazy { ArrayList<E>() }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<E> = getViewHolder(parent, viewType)

    protected abstract fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<E>

    final override fun onBindViewHolder(holder: BaseViewHolder<E>, position: Int) {
        holder.bindView(list[position])
    }

    final override fun getItemCount() = this.list.size

    fun getDataList() = this.list

    fun addAll(data: ArrayList<E>?) {
        if (data != null && data.size > 0) {
            val size = this.list.size
            this.list.addAll(data)
            notifyItemRangeInserted(size, data.size)
        }
    }

    fun clear() {
        val size = this.list.size
        this.list.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun add(e: E) {
        this.list.add(0, e)
        notifyItemInserted(0)
    }

    fun remove(pos: Int) {
        this.list.removeAt(pos)
        notifyItemRemoved(pos)
    }
}
