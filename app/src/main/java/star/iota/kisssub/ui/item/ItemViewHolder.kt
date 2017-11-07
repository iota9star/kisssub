package star.iota.kisssub.ui.item

import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_item.view.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.room.AppDatabaseHelper
import star.iota.kisssub.room.Record
import star.iota.kisssub.ui.details.DetailsFragment
import star.iota.kisssub.utils.ShareUtils
import star.iota.kisssub.utils.ToastUtils


class ItemViewHolder(itemView: View) : BaseViewHolder<Record>(itemView) {

    override fun bindView(bean: Record) {
        itemView.apply {
            textViewTitle.text = bean.title
            textViewSize.text = bean.size
            textViewDate.text = bean.date
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
            textViewShare.setOnClickListener {
                val content = "\n标题：${bean.title}\n\n" +
                        "分类：${bean.category}\n\n" +
                        "文件大小：${bean.size}\n\n" +
                        "发布时间：${bean.date}\n\n" +
                        "字幕组：${bean.sub}\n\n" +
                        "详细地址：${bean.url}\n"
                ShareUtils.share(context, content)
            }
            textViewCollection.setOnClickListener {
                Single.just(AppDatabaseHelper.getInstance(context))
                        .map { it.add(bean) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            ToastUtils.short(context, "收藏：${bean.title} 成功")
                        }, { ToastUtils.short(context, "错误：${it.message} ") })
            }
        }
    }

}