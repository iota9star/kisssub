package star.iota.kisssub.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat
import star.iota.kisssub.R

object ThemeHelper {

    private val THEME_COLOR = "theme_color"
    private val THEME_TINT = "is_tint"
    private val THEME_DARK = "is_dark"
    private val THEME_BANNER = "theme_banner"
    private val PREFERENCE_NAME = "multiple_theme"

    private fun getSharePreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun setColor(context: Context, color: Int) {
        getSharePreference(context).edit()
                .putInt(THEME_COLOR, color)
                .apply()
    }

    fun getColor(context: Context): Int {
        return getSharePreference(context).getInt(THEME_COLOR, ContextCompat.getColor(context, R.color.blue))
    }

    fun isTint(context: Context, isTint: Boolean) {
        getSharePreference(context).edit()
                .putBoolean(THEME_TINT, isTint)
                .apply()
    }

    fun isTint(context: Context): Boolean {
        return getSharePreference(context).getBoolean(THEME_TINT, false)
    }

    fun isDark(context: Context, isDark: Boolean) {
        getSharePreference(context).edit()
                .putBoolean(THEME_DARK, isDark)
                .apply()
    }

    fun isDark(context: Context): Boolean {
        return getSharePreference(context).getBoolean(THEME_DARK, false)
    }

    fun setBanner(context: Context, path: String?) {
        if (path == null) getSharePreference(context).edit().remove(THEME_BANNER).apply()
        else getSharePreference(context).edit()
                .putString(THEME_BANNER, path)
                .apply()
    }

    fun getBanner(context: Context): String {
        return getSharePreference(context).getString(THEME_BANNER, context.getString(R.string.banner))
    }
}