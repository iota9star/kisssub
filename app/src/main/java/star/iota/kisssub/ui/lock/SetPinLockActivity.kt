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

package star.iota.kisssub.ui.lock

import android.view.View
import android.widget.Toast
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import kotlinx.android.synthetic.main.activity_set_pin_lock.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseActivity
import star.iota.kisssub.glide.GlideApp
import star.iota.kisssub.helper.SecurityHelper
import star.iota.kisssub.helper.ThemeHelper
import star.iota.kisssub.widget.M


class SetPinLockActivity : BaseActivity(), View.OnClickListener {

    override fun isFullScreen(): Boolean = true
    override fun getCircularRevealView(): View? = frameLayoutContainer
    private var step = STEP_ONE
    private var pinCode: String = ""

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonLeft -> if (step == STEP_ONE) {
                finish()
            } else if (step == STEP_TWO) {
                pinLockView?.resetPinLockView()
                buttonRight?.visibility = View.GONE
                buttonLeft?.setText(R.string.lock_wait_input)
            }
            R.id.buttonRight -> if (step == STEP_ONE) {
                step = STEP_TWO
                pinLockView?.resetPinLockView()
                buttonRight?.visibility = View.GONE
                buttonLeft?.setText(R.string.lock_wait_input)
            } else if (step == STEP_TWO) {
                SecurityHelper.savePin(this, pinCode)
                SecurityHelper.setLock(this, SecurityHelper.LOCK_TYPE_PIN)
                Toast.makeText(this, "设置完成", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun initEvent() {
        pinLockView?.setPinLockListener(object : PinLockListener {
            override fun onComplete(pin: String) {
                if (step == STEP_ONE) {
                    pinCode = pin
                    buttonRight?.setText(R.string.lock_continue)
                    buttonRight?.visibility = View.VISIBLE
                } else if (step == STEP_TWO) {
                    if (pinCode == pin) {
                        buttonRight?.setText(R.string.lock_finish)
                        buttonRight?.visibility = View.VISIBLE
                    } else {
                        M.create(this@SetPinLockActivity, "前后不一致")
                        pinLockView?.resetPinLockView()
                        buttonRight?.visibility = View.GONE
                    }
                }
            }

            override fun onEmpty() {

            }

            override fun onPinChange(pinLength: Int, intermediatePin: String) {
                if (pinLockView != null && pinLength < pinLockView.pinLength) {
                    buttonRight?.visibility = View.GONE
                }
                if (pinLength == 0) {
                    if (step == STEP_ONE) {
                        buttonLeft?.setText(R.string.lock_cancel)
                    } else if (step == STEP_TWO) {
                        buttonLeft?.setText(R.string.lock_wait_input)
                    }
                } else {
                    if (STEP_ONE == step) {
                        buttonLeft?.setText(R.string.lock_cancel)
                        buttonRight?.setText(R.string.lock_continue)
                    } else if (STEP_TWO == step) {
                        buttonLeft?.setText(R.string.lock_reinput)
                        buttonRight?.setText(R.string.lock_finish)
                    }
                }
            }
        })
    }

    private fun initView() {
        indicatorDots?.indicatorType = IndicatorDots.IndicatorType.FILL_WITH_ANIMATION
        pinLockView?.attachIndicatorDots(indicatorDots)
        buttonRight?.visibility = View.GONE
        buttonLeft?.setText(R.string.lock_cancel)
        buttonLeft?.setOnClickListener(this)
        buttonRight?.setOnClickListener(this)
    }

    override fun getContentViewId(): Int = R.layout.activity_set_pin_lock


    override fun onBackPressed() {
        if (step == STEP_ONE) {
            finish()
        } else if (step == STEP_TWO) {
            step = STEP_ONE
            pinCode = ""
            pinLockView?.resetPinLockView()
            buttonRight?.visibility = View.GONE
            buttonLeft?.setText(R.string.lock_cancel)
        }
    }

    override fun doSome() {
        initBackground()
        initView()
        initEvent()
    }

    private fun initBackground() {
        GlideApp.with(this)
                .load(ThemeHelper.getDynamicBanner(this))
                .into(kenBurnsView)
    }

    companion object {
        private const val STEP_ONE = 1
        private const val STEP_TWO = 2
    }
}
