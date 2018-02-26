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
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_local_no_image.view.*
import org.greenrobot.eventbus.EventBus
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.eventbus.ChangeAdapterEvent
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.helper.SearchHelper
import star.iota.kisssub.room.AppDatabaseHelper
import star.iota.kisssub.room.Record
import star.iota.kisssub.ui.details.DetailsFragment
import star.iota.kisssub.ui.item.search.SearchFragment
import star.iota.kisssub.utils.SendUtils
import star.iota.kisssub.utils.ToastUtils
import star.iota.kisssub.widget.MessageBar


class NoImageViewHolder(itemView: View) : BaseViewHolder<Record>(itemView) {

    override fun bindView(bean: Record) {
        itemView?.apply {
            textViewTitle?.text = bean.title
            textViewCategory?.text = bean.category
            textViewSub?.text = bean.sub
            textViewSub?.setOnClickListener {
                if (!bean.sub.isNullOrBlank()) {
                    (context as AppCompatActivity).addFragmentToActivity(SearchFragment.newInstance(bean.sub!!, bean.sub!!, SearchHelper.getParam(context)), R.id.frameLayoutContainer)
                }
            }
            textViewTitle?.setOnClickListener {
                if (!bean.title.isNullOrBlank() && !bean.url.isNullOrBlank()) {
                    (context as AppCompatActivity).addFragmentToActivity(DetailsFragment.newInstance(bean.title!!, bean.url!!), R.id.frameLayoutContainer)
                }
            }
            buttonDownload?.setOnClickListener {
                SendUtils.copy(context!!, bean.title, bean.magnet)
                SendUtils.open(context!!, bean.magnet)
                MessageBar.create(context!!, "已复制到剪切板，并尝试打开本地应用")
            }
            frameLayoutContainer?.setOnLongClickListener {
                AlertDialog.Builder(context)
                        .setTitle(bean.title)
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("o(*≧▽≦)ツ\n从收藏移除？")
                        .setNegativeButton("移除", { _, _ ->
                            Single.just(AppDatabaseHelper.getInstance(context))
                                    .map { it.deleteRecord(bean) }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        EventBus.getDefault().post(ChangeAdapterEvent(ChangeAdapterEvent.DELETE, adapterPosition))
                                        ToastUtils.short(context, "移除成功")
                                    }, {
                                        MessageBar.create(context, "错误：${it?.message}")
                                    })
                        })
                        .show()
                true
            }
        }
    }
}
