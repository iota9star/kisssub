package star.iota.kisssub.ui.play

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder


class PlayAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list: ArrayList<FanBean> = ArrayList()
    override fun getItemCount(): Int = list.size
    override fun getItemViewType(position: Int): Int = list[position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder?
            = when (viewType) {
        FanBean.WEEK -> WeekViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fan_week, parent, false))
        FanBean.FAN -> FanViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fan, parent, false))
        else -> null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as BaseViewHolder<FanBean>).bindView(list[position])
    }

    fun addAll(list: ArrayList<FanBean>) {
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