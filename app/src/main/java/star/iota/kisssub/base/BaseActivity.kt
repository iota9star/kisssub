package star.iota.kisssub.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import com.afollestad.aesthetic.Aesthetic

abstract class BaseActivity : AppCompatActivity() {

    protected open fun isFullScreen(): Boolean {
        return false
    }

    abstract fun getContentViewId(): Int
    abstract fun doSome()
    override fun onCreate(savedInstanceState: Bundle?) {
        if (isFullScreen()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        Aesthetic.attach(this)
        super.onCreate(savedInstanceState)
        setContentView(getContentViewId())
        doSome()
        title = "o(*≧▽≦)ツ 嗨！你好吗"
    }

    override fun onResume() {
        super.onResume()
        Aesthetic.resume(this)
    }

    override fun onPause() {
        Aesthetic.pause(this)
        super.onPause()
    }
}