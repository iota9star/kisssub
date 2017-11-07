package star.iota.kisssub.ui.rss

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.room.Record

class RssAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list: ArrayList<Record> = ArrayList()
    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = if (viewType == IMAGE) RssWithImageViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rss_with_image, parent, false))
    else RssNoImageViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rss_no_image, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as BaseViewHolder<Record>).bindView(list[position])
    }

    override fun getItemViewType(position: Int): Int = list[position].type

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

    companion object {
        val IMAGE = 1
        val NO_IMAGE = 2
    }

}