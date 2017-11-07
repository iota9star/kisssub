package star.iota.kisssub.ui.collection

import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_local_no_image.view.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.base.Callback
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.room.AppDatabaseHelper
import star.iota.kisssub.room.Record
import star.iota.kisssub.ui.details.DetailsFragment
import star.iota.kisssub.ui.item.ItemFragment
import star.iota.kisssub.utils.ToastUtils
import star.iota.kisssub.widget.MessageBar


class NoImageViewHolder(itemView: View) : BaseViewHolder<Record>(itemView) {

    override fun bindView(bean: Record,callback: Callback?) {
        itemView.apply {
            textViewTitle.text = bean.title
            textViewCategory.text = bean.category
            textViewSub.text = bean.sub
            textViewSub.setOnClickListener {
                if (!bean.sub.isNullOrBlank()) {
                    (context as AppCompatActivity).addFragmentToActivity(ItemFragment.newSearchInstance(bean.sub!!, bean.sub!!), R.id.frameLayoutContainer)
                }
            }
            textViewTitle.setOnClickListener {
                if (!bean.title.isNullOrBlank() && !bean.url.isNullOrBlank()) {
                    (context as AppCompatActivity).addFragmentToActivity(DetailsFragment.newInstance(bean.title!!, bean.url!!), R.id.frameLayoutContainer)
                }
            }
            frameLayoutContainer.setOnLongClickListener {
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