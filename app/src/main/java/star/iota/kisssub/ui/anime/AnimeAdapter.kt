package star.iota.kisssub.ui.anime

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import star.iota.kisssub.R
import star.iota.kisssub.room.Record

class AnimeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list: ArrayList<Record> = ArrayList()
    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = AnimeViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anime, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as AnimeViewHolder).bindView(list[position])
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

}