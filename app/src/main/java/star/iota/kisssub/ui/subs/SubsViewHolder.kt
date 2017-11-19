/*
 *
 *  *    Copyright 2017. iota9star
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

package star.iota.kisssub.ui.subs

import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.item_sub.view.*
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.helper.ThemeHelper
import star.iota.kisssub.ui.item.search.SearchFragment
import star.iota.kisssub.ui.item.search.SearchHelper

class SubsViewHolder(itemView: View) : BaseViewHolder<String>(itemView) {

    override fun bindView(bean: String) {
        itemView.apply {
            textViewSub.text = bean
            val drawable = ContextCompat.getDrawable(context, R.drawable.bg_border) as GradientDrawable
            drawable.setColor(ThemeHelper.getAccentColor(context))
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            textViewSub.setCompoundDrawables(drawable, null, null, null)
            textViewSub.setOnClickListener {
                (context as AppCompatActivity).addFragmentToActivity(SearchFragment.newInstance(bean, bean, SearchHelper.getParam(context)), R.id.frameLayoutContainer)
            }
        }
    }

}