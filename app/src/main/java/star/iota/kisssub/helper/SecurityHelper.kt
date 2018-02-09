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

package star.iota.kisssub.helper

import android.content.Context
import android.content.SharedPreferences

object SecurityHelper {

    private const val PIN = "pin"
    private const val LOCK = "lock"
    private const val FINGERPRINT = "fingerprint"
    private const val PREFERENCE_NAME = "security"

    const val LOCK_TYPE_NONE = 0
    const val LOCK_TYPE_PIN = 1
    const val LOCK_TYPE_PATTERN = 2

    private fun getSharePreference(context: Context): SharedPreferences =
            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getPin(context: Context): String = getSharePreference(context).getString(PIN, "")

    fun savePin(context: Context, pin: String) {
        getSharePreference(context).edit()
                .putString(PIN, pin)
                .apply()
    }

    fun isLock(context: Context): Int = getSharePreference(context).getInt(LOCK, LOCK_TYPE_NONE)

    fun setLock(context: Context, lock: Int) {
        getSharePreference(context).edit()
                .putInt(LOCK, lock)
                .apply()
    }

    fun isOpenFingerprint(context: Context): Boolean =
            getSharePreference(context).getBoolean(FINGERPRINT, false)

    fun setFingerprint(context: Context, open: Boolean) {
        getSharePreference(context).edit()
                .putBoolean(FINGERPRINT, open)
                .apply()
    }
}
