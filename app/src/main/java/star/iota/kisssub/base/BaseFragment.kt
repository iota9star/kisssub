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

package star.iota.kisssub.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import star.iota.kisssub.eventbus.ChangeContentBackgroundEvent
import star.iota.kisssub.ext.exit
import star.iota.kisssub.glide.GlideApp
import star.iota.kisssub.ui.settings.ThemeHelper

abstract class BaseFragment : Fragment(), View.OnTouchListener {


    protected abstract fun getContainerViewId(): Int

    protected abstract fun doSome()

    private var preTitle: String? = null

    protected fun setToolbarTitle(title: CharSequence?) {
        (this.activity!! as BaseActivity).supportActionBar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        preTitle = (this.activity!! as BaseActivity).supportActionBar?.title?.toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getContainerViewId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.setOnTouchListener(this)
        doSome()
        setContentBackground()
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return true
    }

    private fun setContentBackground() {
        val bg = getBackgroundView()
        val mask = getMaskView()
        if (bg == null || mask == null) return
        GlideApp.with(this)
                .load(ThemeHelper.getContentBanner(context!!))
                .into(bg)
        mask.alpha = ThemeHelper.getContentMaskAlpha(context!!)
        if (ThemeHelper.isDark(context!!)) {
            mask.setBackgroundColor(ThemeHelper.getContentMaskColorDark(context!!))
        } else {
            mask.setBackgroundColor(ThemeHelper.getContentMaskColor(context!!))
        }
    }

    abstract fun getBackgroundView(): ImageView?
    abstract fun getMaskView(): View?

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onContentBackgroundChange(event: ChangeContentBackgroundEvent) {
        setContentBackground()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (preTitle != null) {
            setToolbarTitle(preTitle!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    protected fun finish() {
        (activity!! as AppCompatActivity).exit()
    }
}