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

package star.iota.kisssub.ui.settings

import android.content.Intent
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import com.afollestad.aesthetic.Aesthetic
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import kotlinx.android.synthetic.main.fragment_settings_main.*
import org.greenrobot.eventbus.EventBus
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.eventbus.ChangeContentBackgroundEvent
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.helper.OfficialHelper
import star.iota.kisssub.helper.SecurityHelper
import star.iota.kisssub.helper.ThemeHelper
import star.iota.kisssub.ui.lock.SetPinLockActivity
import star.iota.kisssub.utils.ViewContextUtils
import star.iota.kisssub.widget.M

class SettingsMainFragment : BaseFragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    companion object {
        fun newInstance() = SettingsMainFragment()
    }

    override fun doSome() {
        initEvent()
        initSwitchCompat()
    }

    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask
    override fun getContainerViewId(): Int = R.layout.fragment_settings_main
    override fun isShowCircularReveal() = false

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.linearLayoutThemeColor -> {
                ViewContextUtils.getAppCompatActivity(viewMask)?.addFragmentToActivity(SettingsThemeColorFragment.newInstance(getString(R.string.settings_theme_custom_color)), R.id.frameLayoutContainer)
            }
            R.id.linearLayoutPinLock -> showPinLockSetting()
            R.id.linearLayoutFingerprintLock -> showFingerprintSetting()
            R.id.textViewDynamicBackground -> {
                ViewContextUtils.getAppCompatActivity(viewMask)?.addFragmentToActivity(SettingsDynamicBackgroundFragment.newInstance(getString(R.string.settings_theme_dynamic_background)), R.id.frameLayoutContainer)
            }
            R.id.textViewContentBackground -> {
                ViewContextUtils.getAppCompatActivity(viewMask)?.addFragmentToActivity(SettingsContentBackgroundFragment.newInstance(getString(R.string.settings_theme_content_background)), R.id.frameLayoutContainer)
            }
        }
    }

    override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
        when (button?.id) {
            R.id.switchCompatNightly -> {
                if (isChecked) {
                    Aesthetic.get()
                            .activityTheme(R.style.AppThemeDark)
                            .isDark(true)
                            .textColorPrimary(ThemeHelper.getPrimaryTextColorDark(context!!))
                            .textColorSecondary(ThemeHelper.getSecondaryTextColorDark(context!!))
                            .apply()
                } else {
                    Aesthetic.get()
                            .activityTheme(R.style.AppTheme)
                            .isDark(false)
                            .textColorPrimary(ThemeHelper.getPrimaryTextColor(context!!))
                            .textColorSecondary(ThemeHelper.getSecondaryTextColor(context!!))
                            .apply()
                }
                EventBus.getDefault().post(ChangeContentBackgroundEvent())
            }
            R.id.switchCompatAcceptOfficialContentBackground -> {
                OfficialHelper.acceptOfficialContentBackground(context!!, isChecked)
            }
            R.id.switchCompatAcceptOfficialDynamicBackground -> {
                OfficialHelper.acceptOfficialDynamicBackground(context!!, isChecked)
            }
        }
    }

    private fun initEvent() {
        linearLayoutPinLock?.setOnClickListener(this)
        linearLayoutFingerprintLock?.setOnClickListener(this)
        textViewDynamicBackground?.setOnClickListener(this)
        textViewContentBackground?.setOnClickListener(this)
        linearLayoutThemeColor?.setOnClickListener(this)
    }


    private fun showPinLockSetting() {
        if (SecurityHelper.isLock(context!!) != SecurityHelper.LOCK_TYPE_PIN) {
            startActivity(Intent(context!!, SetPinLockActivity::class.java))
        } else {
            AlertDialog.Builder(context!!)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("设置 PIN 保护")
                    .setPositiveButton("关闭 PIN") { _, _ ->
                        SecurityHelper.setLock(context!!, SecurityHelper.LOCK_TYPE_NONE)
                        SecurityHelper.savePin(context!!, "")
                    }
                    .setNegativeButton("重新设置") { _, _ ->
                        startActivity(Intent(context!!, SetPinLockActivity::class.java))
                    }
                    .show()
        }
    }

    private fun showFingerprintSetting() {
        if (SecurityHelper.isLock(context!!) == SecurityHelper.LOCK_TYPE_NONE) {
            M.create(context!!, "请先设置至少一种解锁方式")
            return
        }
        val fingerprintIdentify = FingerprintIdentify(context!!)
        if (!fingerprintIdentify.isHardwareEnable) {
            M.create(context!!, "您的设备可能不支持指纹解锁")
            return
        }
        if (!fingerprintIdentify.isRegisteredFingerprint) {
            M.create(activity(),
                    "您可能还没有设置指纹，是否前往设置",
                    View.OnClickListener {
                        startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                    })
            return
        }
        val isOpen = SecurityHelper.isOpenFingerprint(context!!)
        AlertDialog.Builder(context!!)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(if (isOpen) "关闭指纹识别支持" else "开启指纹识别支持")
                .setPositiveButton("嗯") { _, _ ->
                    if (isOpen) {
                        SecurityHelper.setFingerprint(context!!, false)
                    } else {
                        SecurityHelper.setFingerprint(context!!, true)
                    }
                }
                .show()
    }

    private fun initSwitchCompat() {
        Aesthetic.get()
                .isDark
                .take(1)
                .subscribe {
                    switchCompatNightly?.isChecked = it
                }
        switchCompatNightly?.setOnCheckedChangeListener(this)
        switchCompatAcceptOfficialContentBackground?.isChecked = OfficialHelper.acceptOfficialContentBackground(context!!)
        switchCompatAcceptOfficialContentBackground?.setOnCheckedChangeListener(this)
        switchCompatAcceptOfficialDynamicBackground?.isChecked = OfficialHelper.acceptOfficialDynamicBackground(context!!)
        switchCompatAcceptOfficialDynamicBackground?.setOnCheckedChangeListener(this)
    }
}
