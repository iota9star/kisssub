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

object OfficialHelper {

    private const val ACCEPT_OFFICIAL_DYNAMIC_BACKGROUND = "accept_official_dynamic_background"
    private const val ACCEPT_OFFICIAL_CONTENT_BACKGROUND = "accept_official_content_background"
    private const val PREFERENCE_NAME = "official"

    private fun getSharePreference(context: Context): SharedPreferences =
            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun acceptOfficialContentBackground(context: Context): Boolean = getSharePreference(context).getBoolean(ACCEPT_OFFICIAL_CONTENT_BACKGROUND, true)

    fun acceptOfficialContentBackground(context: Context, accept: Boolean) {
        getSharePreference(context).edit()
                .putBoolean(ACCEPT_OFFICIAL_CONTENT_BACKGROUND, accept)
                .apply()
    }

    fun acceptOfficialDynamicBackground(context: Context): Boolean = getSharePreference(context).getBoolean(ACCEPT_OFFICIAL_DYNAMIC_BACKGROUND, true)

    fun acceptOfficialDynamicBackground(context: Context, accept: Boolean) {
        getSharePreference(context).edit()
                .putBoolean(ACCEPT_OFFICIAL_DYNAMIC_BACKGROUND, accept)
                .apply()
    }
}
