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

object SearchHelper {

    private const val PARAM_PAN = "param_pan"
    private const val PARAM_COLLECTION = "param_collection"
    private const val PREFERENCE_NAME = "search_param"

    private fun getSharePreference(context: Context): SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun setPan(context: Context, enable: Boolean) {
        getSharePreference(context).edit()
                .putBoolean(PARAM_PAN, enable)
                .apply()
    }

    fun getPan(context: Context): Boolean = getSharePreference(context).getBoolean(PARAM_PAN, false)

    fun setCollection(context: Context, enable: Boolean) {
        getSharePreference(context).edit()
                .putBoolean(PARAM_COLLECTION, enable)
                .apply()
    }

    fun getCollection(context: Context): Boolean = getSharePreference(context).getBoolean(PARAM_COLLECTION, false)

    fun getParam(context: Context): String {
        val pan = if (getPan(context)) {
            "&cloudfile=1"
        } else {
            ""
        }
        val collection = if (getCollection(context)) {
            "&complete=1"
        } else {
            ""
        }
        return pan + collection
    }
}
