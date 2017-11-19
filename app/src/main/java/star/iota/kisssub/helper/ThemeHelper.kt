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

package star.iota.kisssub.helper

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat

object ThemeHelper {

    private val THEME_ACCENT_COLOR = "theme_accent_color"
    private val THEME_PRIMARY_COLOR = "theme_primary_color"
    private val THEME_PRIMARY_TEXT_COLOR = "theme_primary_text_color"
    private val THEME_SECONDARY_TEXT_COLOR = "theme_secondary_text_color"
    private val THEME_PRIMARY_TEXT_COLOR_DARK = "theme_primary_text_color_dark"
    private val THEME_SECONDARY_TEXT_COLOR_DARK = "theme_secondary_text_color_dark"
    private val THEME_TINT = "is_tint"
    private val THEME_DARK = "is_dark"
    private val THEME_DYNAMIC_BANNER = "theme_dynamic_banner"
    private val THEME_CONTENT_BANNER = "theme_content_banner"
    private val THEME_CONTENT_MASK_ALPHA = "theme_content_mask_alpha"
    private val THEME_CONTENT_MASK_COLOR = "theme_content_mask_color"
    private val THEME_CONTENT_MASK_COLOR_DARK = "theme_content_mask_color_dark"
    private val PREFERENCE_NAME = "multiple_theme"

    private fun getSharePreference(context: Context): SharedPreferences =
            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun setContentMaskAlpha(context: Context, alpha: Float) {
        getSharePreference(context).edit()
                .putFloat(THEME_CONTENT_MASK_ALPHA, alpha)
                .apply()
    }

    fun getContentMaskAlpha(context: Context): Float =
            getSharePreference(context).getFloat(THEME_CONTENT_MASK_ALPHA, 1f)

    fun setContentMaskColor(context: Context, color: Int) {
        getSharePreference(context).edit()
                .putInt(THEME_CONTENT_MASK_COLOR, color)
                .apply()
    }

    fun getContentMaskColor(context: Context): Int =
            getSharePreference(context).getInt(THEME_CONTENT_MASK_COLOR, ContextCompat.getColor(context, R.color.white))

    fun setContentMaskColorDark(context: Context, color: Int) {
        getSharePreference(context).edit()
                .putInt(THEME_CONTENT_MASK_COLOR_DARK, color)
                .apply()
    }

    fun getContentMaskColorDark(context: Context): Int =
            getSharePreference(context).getInt(THEME_CONTENT_MASK_COLOR_DARK, ContextCompat.getColor(context, R.color.black))

    fun setAccentColor(context: Context, color: Int) {
        getSharePreference(context).edit()
                .putInt(THEME_ACCENT_COLOR, color)
                .apply()
    }

    fun getAccentColor(context: Context): Int =
            getSharePreference(context).getInt(THEME_ACCENT_COLOR, ContextCompat.getColor(context, R.color.blue))

    fun setPrimaryColor(context: Context, color: Int) {
        getSharePreference(context).edit()
                .putInt(THEME_PRIMARY_COLOR, color)
                .apply()
    }

    fun getPrimaryColor(context: Context): Int =
            getSharePreference(context).getInt(THEME_PRIMARY_COLOR, ContextCompat.getColor(context, R.color.white))

    fun setPrimaryTextColor(context: Context, color: Int) {
        getSharePreference(context).edit()
                .putInt(THEME_PRIMARY_TEXT_COLOR, color)
                .apply()
    }

    fun getPrimaryTextColor(context: Context): Int =
            getSharePreference(context).getInt(THEME_PRIMARY_TEXT_COLOR, ContextCompat.getColor(context, R.color.text_color_primary))

    fun setSecondaryTextColor(context: Context, color: Int) {
        getSharePreference(context).edit()
                .putInt(THEME_SECONDARY_TEXT_COLOR, color)
                .apply()
    }

    fun getSecondaryTextColor(context: Context): Int =
            getSharePreference(context).getInt(THEME_SECONDARY_TEXT_COLOR, ContextCompat.getColor(context, R.color.text_color_secondary))

    fun setPrimaryTextColorDark(context: Context, color: Int) {
        getSharePreference(context).edit()
                .putInt(THEME_PRIMARY_TEXT_COLOR_DARK, color)
                .apply()
    }

    fun getPrimaryTextColorDark(context: Context): Int =
            getSharePreference(context).getInt(THEME_PRIMARY_TEXT_COLOR_DARK, ContextCompat.getColor(context, R.color.text_color_primary_dark))

    fun setSecondaryTextColorDark(context: Context, color: Int) {
        getSharePreference(context).edit()
                .putInt(THEME_SECONDARY_TEXT_COLOR_DARK, color)
                .apply()
    }

    fun getSecondaryTextColorDark(context: Context): Int =
            getSharePreference(context).getInt(THEME_SECONDARY_TEXT_COLOR_DARK, ContextCompat.getColor(context, R.color.text_color_secondary_dark))

    fun isTint(context: Context, isTint: Boolean) {
        getSharePreference(context).edit()
                .putBoolean(THEME_TINT, isTint)
                .apply()
    }

    fun isTint(context: Context): Boolean =
            getSharePreference(context).getBoolean(THEME_TINT, false)

    fun isDark(context: Context, isDark: Boolean) {
        getSharePreference(context).edit()
                .putBoolean(THEME_DARK, isDark)
                .apply()
    }

    fun isDark(context: Context): Boolean =
            getSharePreference(context).getBoolean(THEME_DARK, false)

    fun setDynamicBanner(context: Context, path: String?) {
        if (path == null) getSharePreference(context).edit().remove(THEME_DYNAMIC_BANNER).apply()
        else getSharePreference(context).edit()
                .putString(THEME_DYNAMIC_BANNER, path)
                .apply()
    }

    fun getDynamicBanner(context: Context): String =
            getSharePreference(context).getString(THEME_DYNAMIC_BANNER, context.getString(R.string.dynamic_banner))

    fun setContentBanner(context: Context, path: String?) {
        if (path == null) getSharePreference(context).edit().remove(THEME_CONTENT_BANNER).apply()
        else getSharePreference(context).edit()
                .putString(THEME_CONTENT_BANNER, path)
                .apply()
    }

    fun getContentBanner(context: Context): String =
            getSharePreference(context).getString(THEME_CONTENT_BANNER, context.getString(R.string.content_banner))
}