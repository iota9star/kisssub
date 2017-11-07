package star.iota.kisssub.ui.lock

import android.content.Context
import android.content.SharedPreferences

object SecurityHelper {

    private val PIN = "pin"
    private val LOCK = "lock"
    private val FINGERPRINT = "fingerprint"
    private val PREFERENCE_NAME = "security"

    val LOCK_TYPE_NONE = 0
    val LOCK_TYPE_PIN = 1
    val LOCK_TYPE_PATTERN = 2

    private fun getSharePreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun getPin(context: Context): String {
        return getSharePreference(context).getString(PIN, "")
    }

    fun savePin(context: Context, pin: String) {
        getSharePreference(context).edit()
                .putString(PIN, pin)
                .apply()
    }

    fun isLock(context: Context): Int {
        return getSharePreference(context).getInt(LOCK, LOCK_TYPE_NONE)
    }

    fun setLock(context: Context, lock: Int) {
        getSharePreference(context).edit()
                .putInt(LOCK, lock)
                .apply()
    }

    fun isOpenFingerprint(context: Context): Boolean {
        return getSharePreference(context).getBoolean(FINGERPRINT, false)
    }

    fun setFingerprint(context: Context, open: Boolean) {
        getSharePreference(context).edit()
                .putBoolean(FINGERPRINT, open)
                .apply()
    }
}
