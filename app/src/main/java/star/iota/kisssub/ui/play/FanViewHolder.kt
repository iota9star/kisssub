package star.iota.kisssub.ui.play

import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.item_fan.view.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.ui.item.ItemFragment
import star.iota.kisssub.ui.settings.ThemeHelper
import star.iota.kisssub.widget.MessageBar

class FanViewHolder(itemView: View) : BaseViewHolder<FanBean>(itemView) {
    override fun bindView(bean: FanBean) {
        itemView.apply {
            textViewFan.text = bean.title
            textViewFan.setOnClickListener {
                if (!bean.title.isNullOrBlank()) {
                    (context as AppCompatActivity).addFragmentToActivity(ItemFragment.newSearchInstance(bean.title!!,bean.title!!),R.id.frameLayoutContainer)
                }
            }
            if (bean.tip.isNullOrBlank()) {
                textViewFan.setCompoundDrawables(null, null, null, null)
                textViewFan.setOnLongClickListener(null)
            } else {
                val drawable = ContextCompat.getDrawable(context, R.drawable.bg_border) as GradientDrawable
                drawable.setColor(ThemeHelper.getColor(context))
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                textViewFan.setCompoundDrawables(drawable, null, null, null)
                textViewFan.setOnLongClickListener {
                    MessageBar.create(context, bean.tip)
                    true
                }
            }
        }
    }

}