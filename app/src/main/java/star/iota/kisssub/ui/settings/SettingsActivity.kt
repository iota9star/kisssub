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

import android.support.v7.widget.Toolbar
import android.view.View
import kotlinx.android.synthetic.main.activity_settings.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseActivity
import star.iota.kisssub.ext.replaceFragmentInActivity


class SettingsActivity : BaseActivity() {

    override fun getContentViewId(): Int = R.layout.activity_settings
    override fun getCircularRevealView(): View? = coordinatorLayout

    override fun doSome() {
        initToolbar()
        replaceFragmentInActivity(SettingsMainFragment.newInstance(), R.id.frameLayoutContainer)
    }

    override fun getToolbar(): Toolbar? = toolbar

    private fun initToolbar() {
        toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }
}
