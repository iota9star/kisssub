package star.iota.kisssub.ui.subs

import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.item_sub.view.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.ui.item.ItemFragment
import star.iota.kisssub.ui.settings.ThemeHelper

class SubsViewHolder(itemView: View) : BaseViewHolder<String>(itemView) {

    override fun bindView(bean: String) {
        itemView.apply {
            textViewSub.text = bean
            val drawable = ContextCompat.getDrawable(context, R.drawable.bg_border) as GradientDrawable
            drawable.setColor(ThemeHelper.getColor(context))
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            textViewSub.setCompoundDrawables(drawable, null, null, null)
            textViewSub.setOnClickListener {
                (context as AppCompatActivity).addFragmentToActivity(ItemFragment.newSearchInstance(bean, bean), R.id.frameLayoutContainer)
            }
        }
    }

}