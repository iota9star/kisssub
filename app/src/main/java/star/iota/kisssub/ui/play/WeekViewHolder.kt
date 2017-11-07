package star.iota.kisssub.ui.play

import android.view.View
import kotlinx.android.synthetic.main.item_fan_week.view.*
import star.iota.kisssub.base.BaseViewHolder

class WeekViewHolder(itemView: View) : BaseViewHolder<FanBean>(itemView) {
    override fun bindView(bean: FanBean) {
        itemView.apply {
            textViewWeek.text = bean.title
        }
    }

}