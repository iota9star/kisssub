package star.iota.kisssub.ui.lock

import android.content.Intent
import android.os.CountDownTimer
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint
import kotlinx.android.synthetic.main.fragment_pin_lock.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.ui.main.MainActivity
import star.iota.kisssub.ui.settings.ThemeHelper
import star.iota.kisssub.widget.MessageBar

class PinLockFragment : BaseFragment() {

    companion object {
        fun newInstance(): PinLockFragment {
            return PinLockFragment()
        }
    }

    override fun getContainerViewId(): Int = R.layout.fragment_pin_lock

    private val timer = object : CountDownTimer(1600, 1000) {
        override fun onFinish() {
            welcome()
        }

        override fun onTick(millisUntilFinished: Long) {

        }
    }

    private fun initFingerprint() {
        if (!SecurityHelper.isOpenFingerprint(context!!)) return
        val fingerprintIdentify = FingerprintIdentify(context!!)
        if (!fingerprintIdentify.isFingerprintEnable) return
        fingerprintIdentify.startIdentify(3, object : BaseFingerprint.FingerprintIdentifyListener {
            override fun onSucceed() {
                verifySuccess()
            }

            override fun onNotMatch(i: Int) {
                MessageBar.create(context!!, "指纹识别失败，你还有" + i + "次机会")
            }

            override fun onFailed(b: Boolean) {

            }

            override fun onStartFailedByDeviceLocked() {
                activity!!.finish()
            }
        })

    }

    private fun welcome() {
        startActivity(Intent(context!!, MainActivity::class.java))
        activity!!.finish()
    }

    private fun verifySuccess() {
        timer.start()
        MessageBar.create(context!!, "验证成功，即将前往主页面")
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun initEvent() {
        pinLockView.setPinLockListener(object : PinLockListener {
            private var errorTimes = 0
            override fun onComplete(pin: String) {
                if (SecurityHelper.getPin(context!!) == pin) {
                    errorTimes = 0
                    verifySuccess()
                } else {
                    pinLockView.resetPinLockView()
                    errorTimes++
                    if (errorTimes == 3) {
                        errorTimes = 0
                        activity!!.finish()
                    }
                    MessageBar.create(context!!, "解锁失败，你还有" + (3 - errorTimes) + "次输入机会")
                }
            }

            override fun onEmpty() {

            }

            override fun onPinChange(pinLength: Int, intermediatePin: String) {

            }
        })
    }

    private fun initView() {
        pinLockView.textColor=ThemeHelper.getColor(context!!)
        indicatorDots.indicatorType = IndicatorDots.IndicatorType.FILL_WITH_ANIMATION
        pinLockView.attachIndicatorDots(indicatorDots)
    }

    override fun doSome() {
        initView()
        initEvent()
        initFingerprint()
    }
}
