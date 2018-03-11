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

package star.iota.kisssub.ui.history

import android.view.View
import kotlinx.android.synthetic.main.item_history.view.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.ui.play.PlayFragment
import star.iota.kisssub.utils.ViewContextUtils

class HistoryViewHolder(itemView: View) : BaseViewHolder<HistoryBean>(itemView) {

    override fun bindView(bean: HistoryBean) {
        itemView?.apply {
            val title = bean.title
            if (title == null) {
                textViewDate?.text = "数据错误"
                textViewSeason?.text = "o(*≧▽≦)ツ"
            } else {
                textViewDate?.text = title.substring(0, title.length - 3)
                textViewSeason?.text = title.substring(title.length - 3, title.length)
            }
            linearLayoutContainer?.setOnClickListener {
                val url = bean.url
                if (title != null && url != null) {
                    ViewContextUtils.getAppCompatActivity(this)?.addFragmentToActivity(PlayFragment.newInstance(title, url), R.id.frameLayoutContainer)
                }
            }
        }
    }

}
