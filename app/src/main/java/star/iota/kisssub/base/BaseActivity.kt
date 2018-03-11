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

package star.iota.kisssub.base

import CircularReveal
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.afollestad.aesthetic.Aesthetic

abstract class BaseActivity : AppCompatActivity() {

    protected open fun isFullScreen(): Boolean = false

    open fun getToolbar(): Toolbar? = null

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
        val revealView = getCircularRevealView()
        revealView?.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                v.removeOnLayoutChangeListener(this)
                CircularReveal.create(revealView)
            }
        })
        doSome()
        title = "o(*≧▽≦)ツ 嗨！你好吗"
    }

    open fun getCircularRevealView(): View? = null

    override fun onResume() {
        super.onResume()
        Aesthetic.resume(this)
    }

    override fun onPause() {
        Aesthetic.pause(this)
        super.onPause()
    }
}
