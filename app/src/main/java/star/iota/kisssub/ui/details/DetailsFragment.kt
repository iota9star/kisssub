/*
 *
 *  *    Copyright 2018. iota9star
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

package star.iota.kisssub.ui.details

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zzhoujay.richtext.RichText
import com.zzhoujay.richtext.ig.DefaultImageGetter
import kotlinx.android.synthetic.main.fragment_details.*
import star.iota.kisssub.R
import star.iota.kisssub.base.StringContract
import star.iota.kisssub.base.StringFragment
import star.iota.kisssub.base.StringPresenter
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.helper.SearchHelper
import star.iota.kisssub.ui.item.search.SearchFragment
import star.iota.kisssub.ui.selector.PhotoSelectorActivity
import star.iota.kisssub.ui.selector.PhotoSelectorPreviewActivity
import star.iota.kisssub.utils.SendUtils
import star.iota.kisssub.utils.ShareUtils
import star.iota.kisssub.utils.ViewContextUtils
import star.iota.kisssub.widget.M
import java.util.*

class DetailsFragment : StringFragment<StringPresenter<DetailsBean>, DetailsBean>(), StringContract.View<DetailsBean> {
    override fun getRefreshLayout(): SmartRefreshLayout? = refreshLayout
    override fun getStringPresenter(): StringPresenter<DetailsBean> = DetailsPresenter(this)
    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask
    override fun getContainerViewId(): Int = R.layout.fragment_details

    override fun doOther() {
    }

    override fun success(result: DetailsBean) {
        super.success(result)
        bindView(result)
    }

    private fun resetView() {
        linearLayoutContainer?.visibility = View.GONE
        textViewDetails?.text = ""
        textViewList?.text = ""
        textViewDesc?.text = ""
        flexLayoutTags?.removeAllViews()
    }

    @SuppressLint("InflateParams")
    private fun bindView(bean: DetailsBean) {
        linearLayoutContainer?.visibility = View.VISIBLE
        RichText.from(bean.details)
                .imageGetter(DefaultImageGetter())
                .urlClick {
                    SendUtils.open(context!!, it)
                    true
                }
                .imageClick { urls, pos ->
                    val intent = Intent(activity!!, PhotoSelectorPreviewActivity::class.java)
                    intent.putExtra(PhotoSelectorActivity.PHOTOS_CAN_BE_REMOVE, false)
                    intent.putExtra(PhotoSelectorActivity.FIRST_PHOTO_INDEX, pos)
                    intent.putStringArrayListExtra(PhotoSelectorActivity.SELECTED_STRING_ARRAY_LIST_PHOTOS, urls as ArrayList<String>?)
                    intent.putExtra(PhotoSelectorActivity.SOURCE_ACTIVITY, activity!!::class.java.canonicalName)
                    startActivity(intent)
                }
                .into(textViewDetails)
        RichText.from(bean.tree).imageGetter(DefaultImageGetter()).into(textViewList)
        textViewDesc?.text = bean.desc?.replace("，", "\n")
        bean.tags?.forEach { str ->
            val tag = LayoutInflater.from(activity()).inflate(R.layout.item_details_tag, null) as TextView
            tag.text = str
            tag.setOnClickListener {
                ViewContextUtils.getAppCompatActivity(it)?.addFragmentToActivity(SearchFragment.newInstance("标签：$str", str, SearchHelper.getParam(activity())), R.id.frameLayoutContainer)
            }
            flexLayoutTags?.addView(tag)
        }
        buttonMagnet?.setOnClickListener {
            SendUtils.copy(activity(), arguments?.getString(TITLE), bean.magnet)
            SendUtils.open(activity(), bean.magnet)
            M.create(activity().applicationContext, "已复制到剪切板，并尝试打开本地应用")
        }
        buttonTorrent?.setOnClickListener {
            SendUtils.copy(activity(), arguments?.getString(TITLE), bean.torrent)
            SendUtils.open(activity(), bean.torrent)
            M.create(activity().applicationContext, "已复制到剪切板，并尝试打开本地应用")
        }
        buttonShare?.setOnClickListener {
            ShareUtils.share(activity(), "\n 标题：${arguments?.getString(TITLE)}\n\n" +
                    "磁链：${bean.magnet}\n\n" +
                    "种链：${bean.torrent}")
        }
    }

    companion object {
        fun newInstance(title: String, url: String): DetailsFragment {
            val fragment = DetailsFragment()
            val bundle = Bundle()
            bundle.putString(TITLE, title)
            bundle.putString(URL, url)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onRefresh() {
        resetView()
    }
}
