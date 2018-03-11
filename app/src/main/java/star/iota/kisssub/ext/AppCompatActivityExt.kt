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

package star.iota.kisssub.ext

import android.os.SystemClock
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import star.iota.kisssub.widget.M

fun AppCompatActivity.replaceFragmentInActivity(fragment: Fragment?, @IdRes fragmentContainer: Int) {
    if (fragment == null) return
    supportFragmentManager.beginTransaction()
            .replace(fragmentContainer, fragment)
            .commitAllowingStateLoss()
}

fun AppCompatActivity.addFragmentToActivity(fragment: Fragment?, @IdRes fragmentContainer: Int) {
    if (fragment == null) return
    supportFragmentManager.beginTransaction()
            .add(fragmentContainer, fragment, fragment::class.java.simpleName)
            .addToBackStack(fragment::class.java.simpleName)
            .commitAllowingStateLoss()
}

fun AppCompatActivity.removeFragmentsFromView(@IdRes fragmentContainerId: Int) {
    (findViewById<View>(fragmentContainerId) as ViewGroup).removeAllViews()
}

private val mHints = LongArray(2)

fun AppCompatActivity.exit() {
    if (supportFragmentManager.backStackEntryCount > 0) {
        supportFragmentManager.popBackStack()
    } else {
        System.arraycopy(mHints, 1, mHints, 0, mHints.size - 1)
        mHints[mHints.size - 1] = SystemClock.uptimeMillis()
        M.create(this, "期待下一次与你相遇", View.OnClickListener {
            System.exit(0)
        })
        if (SystemClock.uptimeMillis() - mHints[0] <= 1600) {
            System.exit(0)
        }
    }
}
