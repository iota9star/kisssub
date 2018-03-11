/*
 *
 *  *    Copyright 2018. iota9star
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package star.iota.kisssub.ui.play

import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.item_fan.view.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.helper.SearchHelper
import star.iota.kisssub.helper.ThemeHelper
import star.iota.kisssub.ui.item.search.SearchFragment
import star.iota.kisssub.utils.ViewContextUtils
import star.iota.kisssub.widget.M

class FanViewHolder(itemView: View) : BaseViewHolder<FanBean>(itemView) {
    override fun bindView(bean: FanBean) {
        itemView?.apply {
            val title = bean.title
            textViewFan?.text = title
            textViewFan?.setOnClickListener {
                if (title != null) {
                    ViewContextUtils.getAppCompatActivity(this)?.addFragmentToActivity(SearchFragment.newInstance(title, title, SearchHelper.getParam(context)), R.id.frameLayoutContainer)
                }
            }
            val tip = bean.tip
            if (tip.isNullOrBlank()) {
                textViewFan?.setCompoundDrawables(null, null, null, null)
                textViewFan?.setOnLongClickListener(null)
            } else {
                val drawable = ContextCompat.getDrawable(context, R.drawable.bg_border) as GradientDrawable
                drawable.setColor(ThemeHelper.getAccentColor(context))
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                textViewFan?.setCompoundDrawables(drawable, null, null, null)
                textViewFan?.setOnLongClickListener {
                    M.create(context, tip)
                    true
                }
            }
        }
    }

}
