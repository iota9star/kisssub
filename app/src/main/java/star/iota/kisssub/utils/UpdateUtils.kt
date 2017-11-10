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

package star.iota.kisssub.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.widget.TextView
import star.iota.kisssub.R
import star.iota.kisssub.ui.about.InfoBean
import star.iota.kisssub.widget.MessageBar

object UpdateUtils {
    fun show(context: Context, info: InfoBean, isCheck: Boolean) {
        if (info.type == InfoBean.TYPE_INFO) {
            AlertDialog.Builder(context)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("哔、有一条新消息")
                    .setMessage(info.msg)
                    .setNegativeButton("好的", { dialog, _ ->
                        dialog.dismiss()
                    })
                    .show()
        } else if (info.type == InfoBean.TYPE_UPDATE) {
            if (info.versionCode == context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_CONFIGURATIONS).versionCode) {
                if (isCheck) MessageBar.create(context, "当前已是最新版，无需更新")
            } else {
                val view = LayoutInflater.from(context).inflate(R.layout.dialog_update, null)
                val ver = view.findViewById<TextView>(R.id.textViewVersion)
                val date = view.findViewById<TextView>(R.id.textViewDate)
                val star = view.findViewById<TextView>(R.id.textViewStar)
                val log = view.findViewById<TextView>(R.id.textViewChangeLog)
                ver.text = ("版本号：${info.versionName}")
                log.text = ("更新日志：\n" + info.changeLog)
                star.text = ("推荐指数：${info.star}星")
                date.text = ("更新日期：${info.date}")
                AlertDialog.Builder(context)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("叮咚、有一个新的更新")
                        .setView(view)
                        .setNegativeButton("直接下载", { _, _ ->
                            SendUtils.open(context, info.url)
                        })
                        .setPositiveButton("应用市场下载", { _, _ ->
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.packageName)))
                        })
                        .show()
            }
        }
    }
}