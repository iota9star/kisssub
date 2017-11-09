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

package star.iota.kisssub.ui.collection

import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.CropSquareTransformation
import kotlinx.android.synthetic.main.item_local_anime.view.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.base.Callback
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.glide.GlideApp
import star.iota.kisssub.glide.GlideOptions
import star.iota.kisssub.room.AppDatabaseHelper
import star.iota.kisssub.room.Record
import star.iota.kisssub.ui.item.ItemFragment
import star.iota.kisssub.ui.settings.ThemeHelper
import star.iota.kisssub.utils.DisplayUtils
import star.iota.kisssub.utils.ToastUtils
import star.iota.kisssub.widget.MessageBar

class AnimeViewHolder(itemView: View) : BaseViewHolder<Record>(itemView) {

    override fun bindView(bean: Record, callback: Callback?) {
        itemView.apply {
            GlideApp.with(itemView)
                    .load(bean.cover)
                    .listener(GlidePalette.with(bean.cover)
                            .use(BitmapPalette.Profile.VIBRANT_LIGHT)
                            .intoBackground(imageViewCover, BitmapPalette.Swatch.RGB)
                            .crossfade(true))
                    .apply(GlideOptions.bitmapTransform(CropSquareTransformation()))
                    .into(imageViewCover)
            textViewTitle.text = bean.title
            val drawable = ContextCompat.getDrawable(context, R.drawable.bg_border) as GradientDrawable
            drawable.setColor(ThemeHelper.getAccentColor(context))
            drawable.setBounds(0, 0, DisplayUtils.dp2px(context, 6f), DisplayUtils.dp2px(context, 18f))
            textViewTitle.setCompoundDrawables(drawable, null, null, null)
            linearLayoutContainer.setOnClickListener {
                if (!bean.title.isNullOrBlank()) {
                    (context as AppCompatActivity).addFragmentToActivity(ItemFragment.newSearchInstance(bean.title!!, bean.title!!), R.id.frameLayoutContainer)
                }
            }
            linearLayoutContainer.setOnLongClickListener {
                AlertDialog.Builder(context)
                        .setTitle(bean.title)
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("o(*≧▽≦)ツ\n从收藏移除？")
                        .setNegativeButton("移除", { _, _ ->
                            Single.just(AppDatabaseHelper.getInstance(context))
                                    .map { it.delete(bean) }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        callback?.result(adapterPosition)
                                        ToastUtils.short(context, "移除成功")
                                    }, {
                                        MessageBar.create(context, "错误：${it?.message}")
                                    })
                        })
                        .show()
                true
            }
        }
    }
}