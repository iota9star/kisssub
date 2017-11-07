package star.iota.kisssub.ui.about

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import kotlinx.android.synthetic.main.activity_about.*
import moe.feng.alipay.zerosdk.AlipayZeroSdk
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseActivity
import star.iota.kisssub.glide.GlideApp
import star.iota.kisssub.ui.settings.ThemeHelper
import star.iota.kisssub.utils.SendUtils
import star.iota.kisssub.widget.MessageBar

class AboutActivity : BaseActivity(), View.OnClickListener {
    override fun getContentViewId(): Int = R.layout.activity_about

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.textViewSource -> {
                SendUtils.open(this, getString(R.string.about_kisssub_url))
            }
            R.id.linearLayoutGrade -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + this.packageName)))
            }
            R.id.linearLayoutAlipay -> {
                if (AlipayZeroSdk.hasInstalledAlipayClient(this)) {
                    AlipayZeroSdk.startAlipayClient(this, this.resources.getString(R.string.alipay_code))
                } else {
                    MessageBar.create(this, "你可能没有安装支付宝");
                }
            }
            R.id.linearLayoutWechat -> {
                SendUtils.open(this, getString(R.string.wechat_pay_code))
            }
            R.id.linearLayoutQQ -> {
                SendUtils.open(this, getString(R.string.qq_pay_code))
            }
        }
    }


    private fun initEvent() {
        textViewSource.setOnClickListener(this)
        linearLayoutGrade.setOnClickListener(this)
        linearLayoutAlipay.setOnClickListener(this)
        linearLayoutWechat.setOnClickListener(this)
        linearLayoutQQ.setOnClickListener(this)
    }

    override fun doSome() {
        initEvent()
        GlideApp.with(this)
                .load(ThemeHelper.getBanner(this))
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