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

package star.iota.kisssub.ui.collection

import android.support.v7.app.AlertDialog
import android.view.View
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_local_with_image.view.*
import org.greenrobot.eventbus.EventBus
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.eventbus.ChangeAdapterEvent
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.glide.GlideApp
import star.iota.kisssub.room.AppDatabaseHelper
import star.iota.kisssub.room.Record
import star.iota.kisssub.ui.details.DetailsFragment
import star.iota.kisssub.ui.rss.data.RssFragment
import star.iota.kisssub.utils.SendUtils
import star.iota.kisssub.utils.ViewContextUtils
import star.iota.kisssub.widget.M

class WithImageViewHolder(itemView: View) : BaseViewHolder<Record>(itemView) {
    override fun bindView(bean: Record) {
        itemView?.apply {
            val activity = ViewContextUtils.getAppCompatActivity(this)
            GlideApp.with(this)
                    .load(bean.cover)
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .fallback(R.mipmap.ic_launcher)
                    .into(imageViewCover)
            val title = bean.title
            textViewTitle?.text = title
            textViewCategory?.text = bean.category
            val sub = bean.sub
            textViewSub?.text = sub
            textViewSub?.setOnClickListener {
                if (sub != null) {
                    activity?.addFragmentToActivity(RssFragment.newInstance(sub), R.id.frameLayoutContainer)
                }
            }
            textViewTitle?.setOnClickListener {
                val url = bean.url
                if (title != null && url != null) {
                    activity?.addFragmentToActivity(DetailsFragment.newInstance(title, url), R.id.frameLayoutContainer)
                }
            }
            buttonDownload?.setOnClickListener {
                SendUtils.copy(activity, title, bean.magnet)
                SendUtils.open(activity, bean.magnet)
                M.create(activity?.applicationContext, "已复制到剪切板，并尝试打开本地应用")
            }
            linearLayoutContainer?.setOnLongClickListener {
                AlertDialog.Builder(context)
                        .setTitle(title)
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("o(*≧▽≦)ツ\n从收藏移除？")
                        .setNegativeButton("移除", { _, _ ->
                            Single.just(AppDatabaseHelper.getInstance(context))
                                    .map { it.deleteRecord(bean) }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        EventBus.getDefault().post(ChangeAdapterEvent(ChangeAdapterEvent.DELETE, adapterPosition))
                                        M.create(activity?.applicationContext, "移除成功")
                                    }, {
                                        M.create(activity?.applicationContext, "错误：${it?.message}")
                                    })
                        })
                        .show()
                true
            }
        }
    }
}
