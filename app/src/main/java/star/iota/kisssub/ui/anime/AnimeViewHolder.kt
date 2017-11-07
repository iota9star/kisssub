package star.iota.kisssub.ui.anime

import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.CropSquareTransformation
import kotlinx.android.synthetic.main.item_anime.view.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.glide.GlideApp
import star.iota.kisssub.glide.GlideOptions
import star.iota.kisssub.room.AppDatabaseHelper
import star.iota.kisssub.room.Record
import star.iota.kisssub.ui.item.ItemFragment
import star.iota.kisssub.utils.ToastUtils

class AnimeViewHolder(itemView: View) : BaseViewHolder<Record>(itemView) {

    override fun bindView(bean: Record) {
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
            imageViewCover.setOnClickListener {
                if (!bean.title.isNullOrBlank()) {
                    (context as AppCompatActivity).addFragmentToActivity(ItemFragment.newSearchInstance(bean.title!!, bean.title!!), R.id.frameLayoutContainer)
                }
            }
            imageViewCover.setOnLongClickListener {
                AlertDialog.Builder(context)
                        .setTitle(bean.title)
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("o(*≧▽≦)ツ\n收藏当前番组？")
                        .setPositiveButton("收藏", { _, _ ->
                            Single.just(AppDatabaseHelper.getInstance(context))
                                    .map { it.add(bean) }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        ToastUtils.short(context, "收藏：${bean.title} 成功")
                                    }, { ToastUtils.short(context, "错误：${it.message}") })
                        })
                        .setNegativeButton("取消", { dialog, _ ->
                            dialog.dismiss()
                        })
                        .show()
                true
            }
        }
    }
}