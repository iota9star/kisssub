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

package star.iota.kisssub.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView


object DisplayUtils {

    fun getScreenHeight(context: Context): Int = context.resources.displayMetrics.heightPixels

    fun getScreenWidth(context: Context): Int = context.resources.displayMetrics.widthPixels

    fun dp2px(context: Context, dp: Float): Int = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics) + 0.5f).toInt()

    fun px2dp(context: Context, px: Float): Int = (px / context.resources.displayMetrics.density + 0.5f).toInt()

    fun px2sp(context: Context, px: Float): Int = (px / context.resources.displayMetrics.scaledDensity + 0.5f).toInt()

    fun sp2px(context: Context, sp: Float): Int = (sp * context.resources.displayMetrics.scaledDensity + 0.5f).toInt()

    fun getDialogWidth(context: Context): Int = context.resources.displayMetrics.widthPixels - 100

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
