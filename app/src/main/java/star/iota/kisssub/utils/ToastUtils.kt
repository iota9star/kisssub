package star.iota.kisssub.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {
    fun short(context: Context, content: String?) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    fun long(context: Context, content: String?) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show()
    }
}