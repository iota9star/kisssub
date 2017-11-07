package star.iota.kisssub.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri

object SendUtils {
    fun open(context: Context, url: String?) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            context.startActivity(intent)
        } catch (e: Exception) {
        }
    }

    fun copy(context: Context, label: String?, content: String?) {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val data = ClipData.newPlainText(label, content)
        manager.primaryClip = data
    }
}