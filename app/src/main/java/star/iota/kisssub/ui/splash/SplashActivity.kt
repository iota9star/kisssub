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

package star.iota.kisssub.ui.splash

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.CountDownTimer
import android.util.TypedValue
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.NavigationViewMode
import kotlinx.android.synthetic.main.activity_splash.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseActivity
import star.iota.kisssub.ext.removeFragmentsFromView
import star.iota.kisssub.ext.replaceFragmentInActivity
import star.iota.kisssub.glide.GlideApp
import star.iota.kisssub.ui.lock.PinLockFragment
import star.iota.kisssub.ui.lock.SecurityHelper
import star.iota.kisssub.ui.main.MainActivity
import star.iota.kisssub.ui.settings.ThemeHelper

class SplashActivity : BaseActivity() {
    override fun getContentViewId(): Int = R.layout.activity_splash

    override fun isFullScreen(): Boolean {
        return true
    }

    private val countDownTimer = object : CountDownTimer(4800, 1000) {
        override fun onTick(l: Long) {

        }

        override fun onFinish() {
            checkSecurity()
        }
    }
    private var isCheck = false
    private fun checkSecurity() {
        if (SecurityHelper.isLock(this) == SecurityHelper.LOCK_TYPE_PIN) {
            if (isCheck) return
            removeFragmentsFromView(R.id.frameLayoutContainer)
            replaceFragmentInActivity(PinLockFragment.newInstance(), R.id.frameLayoutContainer)
        } else {
            countDownTimer.cancel()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        isCheck = true
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }

    override fun doSome() {
        if (Aesthetic.isFirstTime()) {
            Aesthetic.get()
                    .activityTheme(R.style.AppTheme)
                    .textColorPrimaryRes(R.color.text_color_primary)
                    .textColorSecondaryRes(R.color.text_color_secondary)
                    .colorPrimaryRes(R.color.white)
                    .colorAccentRes(R.color.blue)
                    .navigationViewMode(NavigationViewMode.SELECTED_ACCENT)
                    .colorStatusBarAuto()
                    .colorNavigationBarAuto()
                    .apply()
        }
        val typedValue = TypedValue()
        this.theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
        val colors = intArrayOf(0x00000000, typedValue.data)
        val mask = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
        viewMask.background = mask
        GlideApp.with(this)
                .load(ThemeHelper.getDynamicBanner(this))
                .into(kenBurnsView)
        imageViewIcon.setOnClickListener {
            checkSecurity()
        }
        countDownTimer.start()
    }

}