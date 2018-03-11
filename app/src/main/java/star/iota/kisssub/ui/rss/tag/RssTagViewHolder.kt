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

package star.iota.kisssub.ui.rss.tag

import android.annotation.SuppressLint
import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.widget.ListPopupWindow
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_rss_tag.view.*
import org.greenrobot.eventbus.EventBus
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.eventbus.ChangeAdapterEvent
import star.iota.kisssub.room.AppDatabaseHelper
import star.iota.kisssub.room.RssTag
import star.iota.kisssub.utils.DisplayUtils
import star.iota.kisssub.utils.ViewContextUtils
import star.iota.kisssub.widget.M


class RssTagViewHolder(itemView: View) : BaseViewHolder<RssTag>(itemView) {

    override fun bindView(bean: RssTag) {
        itemView?.apply {
            textViewTag?.text = bean.tag
            imageViewMenu?.setOnClickListener {
                showMenu(bean, imageViewMenu)
            }
        }
    }

    private fun showMenu(bean: RssTag, view: View) {
        val context = ViewContextUtils.getAppCompatActivity(view) ?: return
        val listPopupWindow = ListPopupWindow(context)
        listPopupWindow.setAdapter(ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,
                arrayOf("修改", "删除")))
        listPopupWindow.setOnItemClickListener { _, _, pos, _ ->
            if (pos == 0) {
                showModifyDialog(context, bean)
            } else if (pos == 1) {
                Single.just(AppDatabaseHelper.getInstance(context))
                        .map { it.deleteRssTag(bean) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            M.create(context, "删除成功")
                            EventBus.getDefault().post(ChangeAdapterEvent(ChangeAdapterEvent.DELETE, adapterPosition))
                        }, {
                            M.create(context, "删除失败：${it?.message}")
                        })
            }
            listPopupWindow.dismiss()
        }
        listPopupWindow.width = DisplayUtils.dp2px(context, 96f)
        listPopupWindow.anchorView = view
        listPopupWindow.isModal = true
        listPopupWindow.show()
    }

    @SuppressLint("InflateParams")
    private fun showModifyDialog(context: Context, bean: RssTag) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_rss_tag, null)
        val editText = view.findViewById<TextInputEditText>(R.id.textInputEditTextRssTag)
        editText.setText(bean.tag)
        val dialog = AlertDialog.Builder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("修改订阅")
                .setView(view)
                .setNegativeButton("修改", null)
                .setPositiveButton("取消", { dialog, _ ->
                    dialog.dismiss()
                })
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            val tag = editText.text.toString()
            if (tag.trim().isBlank()) {
                editText.error = "不能为空"
            } else {
                bean.tag = tag
                Single.just(AppDatabaseHelper.getInstance(context))
                        .map { it.addRssTag(bean) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            M.create(context, "修改成功")
                            EventBus.getDefault().post(ChangeAdapterEvent(ChangeAdapterEvent.MODIFY, -1))
                            dialog.dismiss()
                        }, {
                            M.create(context, "修改失败：${it?.message}")
                        })

            }
        }
    }
}
