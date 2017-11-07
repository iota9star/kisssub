package star.iota.kisssub.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView


object DisplayUtils {

    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun dp2px(context: Context, dp: Float): Int {
        return (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics) + 0.5f).toInt()
    }

    fun px2dp(context: Context, px: Float): Int {
        return (px / context.resources.displayMetrics.density + 0.5f).toInt()
    }

    fun px2sp(context: Context, px: Float): Int {
        return (px / context.resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }

    fun sp2px(context: Context, sp: Float): Int {
        return (sp * context.resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }

    fun getDialogWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels - 100
    }

    fun tintImageView(view: View, color: Int) {
        if (view is ViewGroup) {
            (0 until view.childCount)
                    .map { view.getChildAt(it) }
                    .forEach {
                        when (it) {
                            is ViewGroup -> tintImageView(it, color)
                            is ImageView -> it.setColorFilter(color)
                        }
                    }
        } else if (view is ImageView) {
            view.setColorFilter(color)
        }
    }
}
