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

package star.iota.kisssub.ui.item

import android.view.View
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_item.view.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.helper.SearchHelper
import star.iota.kisssub.room.AppDatabaseHelper
import star.iota.kisssub.room.Record
import star.iota.kisssub.ui.details.DetailsFragment
import star.iota.kisssub.ui.item.search.SearchFragment
import star.iota.kisssub.utils.SendUtils
import star.iota.kisssub.utils.ShareUtils
import star.iota.kisssub.utils.ViewContextUtils
import star.iota.kisssub.widget.M


class ItemViewHolder(itemView: View) : BaseViewHolder<Record>(itemView) {
    override fun bindView(bean: Record) {
        itemView?.apply {
            val title = bean.title
            textViewTitle?.text = title
            textViewSize?.text = bean.size
            textViewDate?.text = bean.date
            textViewCategory?.text = bean.category
            val sub = bean.sub
            textViewSub?.text = sub
            val activity = ViewContextUtils.getAppCompatActivity(this)
            textViewSub?.setOnClickListener {
                if (sub != null) {
                    activity?.addFragmentToActivity(SearchFragment.newInstance(sub, sub, SearchHelper.getParam(context)), R.id.frameLayoutContainer)
                }
            }
            val url = bean.url
            textViewTitle?.setOnClickListener {
                if (title != null && url != null) {
                    activity?.addFragmentToActivity(DetailsFragment.newInstance(title, url), R.id.frameLayoutContainer)
                }
            }
            textViewDownload?.setOnClickListener {
                SendUtils.copy(activity, title, bean.magnet)
                SendUtils.open(activity, bean.magnet)
                M.create(activity?.applicationContext, "已复制到剪切板，并尝试打开本地应用")
            }
            textViewShare?.setOnClickListener {
                val content = "\n标题：$title\n\n" +
                        "分类：${bean.category}\n\n" +
                        "文件大小：${bean.size}\n\n" +
                        "发布时间：${bean.date}\n\n" +
                        "字幕组：$sub\n\n" +
                        "磁链：${bean.magnet}\n\n" +
                        "详情：$url\n"
                ShareUtils.share(activity, content)
                SendUtils.copy(activity, title, bean.magnet)
                M.create(activity?.applicationContext, "磁链已复制到剪切板")
            }
            textViewCollection?.setOnClickListener {
                Single.just(AppDatabaseHelper.getInstance(context))
                        .map { it.addRecord(bean) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            M.create(activity?.applicationContext, "收藏：$title 成功")
                        }, { M.create(activity?.applicationContext, "错误：${it.message} ") })
            }
        }
    }

}
