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

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import kotlinx.android.synthetic.main.activity_about.*
import star.iota.kisssub.KisssubUrl
import star.iota.kisssub.base.BaseActivity
import star.iota.kisssub.helper.ThemeHelper
import star.iota.kisssub.utils.SendUtils
import star.iota.kisssub.utils.UpdateUtils
import star.iota.kisssub.widget.MessageBar

class AboutActivity : BaseActivity(), View.OnClickListener, InfoContract.View {
    override fun success(info: InfoBean) {
        finishLoad()
        UpdateUtils.show(this, info, true)
    }

    override fun error(e: String?) {
        finishLoad()
    }

    override fun noData() {
        finishLoad()
        MessageBar.create(this, "没有获得版本信息")
    }

    private fun finishLoad() {
        isLoading = false
        progressBar.visibility = View.GONE
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
                if (!isLoading) {
                    isLoading = true
                    progressBar.visibility = View.VISIBLE
                    presenter.get(KisssubUrl.UPDATE_URL)
                } else {
                    MessageBar.create(this, "正在加载版本信息中，请等待")
                }
            }
        }
    }


    private fun initEvent() {
        textViewSource.setOnClickListener(this)
        linearLayoutGrade.setOnClickListener(this)
        linearLayoutCheckUpdate.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unsubscribe()
    }

    private var isLoading = false
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
        fun newInstance(): AboutActivity = AboutActivity()
    }
}