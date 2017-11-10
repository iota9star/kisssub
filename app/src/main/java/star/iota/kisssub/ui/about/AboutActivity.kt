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

package star.iota.kisssub.ui.about

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_about.*
import star.iota.kisssub.KisssubUrl
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseActivity
import star.iota.kisssub.glide.GlideApp
import star.iota.kisssub.ui.settings.ThemeHelper
import star.iota.kisssub.utils.SendUtils
import star.iota.kisssub.widget.MessageBar

class AboutActivity : BaseActivity(), View.OnClickListener, InfoContract.View {
    override fun success(info: InfoBean) {
        if (info.type == InfoBean.TYPE_INFO) {
            MessageBar.create(this, "当前已是最新版，无需更新")
        } else if (info.type == InfoBean.TYPE_UPDATE) {
            showUpdateInfo(info)
        }
    }

    private fun showUpdateInfo(info: InfoBean) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_update, null)
        val ver = view.findViewById<TextView>(R.id.textViewVersion)
        val date = view.findViewById<TextView>(R.id.textViewDate)
        val star = view.findViewById<TextView>(R.id.textViewStar)
        val log = view.findViewById<TextView>(R.id.textViewChangeLog)
        ver.text = ("版本号：${info.versionName}")
        log.text = ("更新日志：" + info.changeLog)
        star.text = ("推荐指数：${info.star}")
        date.text = ("更新日期：${info.date}")
        AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("有一个新的更新")
                .setView(view)
                .setNegativeButton("直接下载", { _, _ ->
                    SendUtils.open(this@AboutActivity, info.url)
                })
                .setPositiveButton("应用市场下载", { _, _ ->
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + this.packageName)))
                })
                .show()
    }

    override fun error(e: String?) {
    }

    override fun noData() {
    }

    override fun getContentViewId(): Int = R.layout.activity_about

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.textViewSource -> {
                SendUtils.open(this, getString(R.string.about_kisssub_url))
            }
            R.id.linearLayoutGrade -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + this.packageName)))
            }
            R.id.linearLayoutCheckUpdate -> {
                presenter.get(KisssubUrl.UPDATE_URL)
            }
        }
    }


    private fun initEvent() {
        textViewSource.setOnClickListener(this)
        linearLayoutGrade.setOnClickListener(this)
        linearLayoutCheckUpdate.setOnClickListener(this)
    }

    private lateinit var presenter: InfoPresenter
    override fun doSome() {
        presenter = InfoPresenter(this)
        initEvent()
        GlideApp.with(this)
                .load(ThemeHelper.getDynamicBanner(this))
                .into(kenBurnsView)
        try {
            val packageInfo = this.packageManager.getPackageInfo(this.packageName, PackageManager.GET_CONFIGURATIONS)
            textViewVersion.text = ("${packageInfo.versionName} : ${packageInfo.versionCode}")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    companion object {
        fun newInstance(): AboutActivity {
            return AboutActivity()
        }
    }
}