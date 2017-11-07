package star.iota.kisssub.ui.collection

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.base.Callback
import star.iota.kisssub.room.Record

class CollectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list: ArrayList<Record> = ArrayList()
    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return when (viewType) {
            Record.FAN -> AnimeViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_local_anime, parent, false))
            Record.NO_IMAGE -> NoImageViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_local_no_image, parent, false))
            Record.WITH_IMAGE -> WithImageViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_local_with_image, parent, false))
            else -> null
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder == null) return
        (holder as BaseViewHolder<Record>).bindView(list[position], object : Callback {
            override fun result(result: Any?) {
                remove(result as Int)
            }
        })
    }

    private fun remove(pos: Int) {
        list.removeAt(pos)
        notifyItemRemoved(pos)
    }

    fun addAll(list: ArrayList<Record>) {
        val size = this.list.size
        this.list.addAll(list)
        notifyItemRangeInserted(size, list.size)
    }

    fun clear() {
        val size = list.size
        list.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun getItemViewType(position: Int): Int = list[position].type

}