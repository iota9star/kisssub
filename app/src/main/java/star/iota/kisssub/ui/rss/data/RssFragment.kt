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

package star.iota.kisssub.ui.rss.data

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.fragment_default.*
import star.iota.kisssub.KisssubUrl
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseAdapter
import star.iota.kisssub.base.StringPresenter
import star.iota.kisssub.room.Record


class RssFragment : LazyLoadFragment<StringPresenter<ArrayList<Record>>, ArrayList<Record>, Record>() {
    override fun getRefreshLayout(): SmartRefreshLayout? = refreshLayout

    override fun getRecyclerView(): RecyclerView? = recyclerView

    override fun getAdapter(): BaseAdapter<Record> = baseAdapter

    override fun setupRefreshLayout(refreshLayout: SmartRefreshLayout?) {
        if (isShow() || active) {
            refreshLayout?.autoRefresh()
        }
        refreshLayout?.isEnableLoadMore = false
    }

    override fun getStringPresenter(): StringPresenter<ArrayList<Record>> = RssPresenter(this)

    override fun isStableUrl() = true
    override fun getStableUrl() = (arguments?.getString(URL, "")
            ?: "") + (arguments?.getString(SUFFIX, "") ?: "")

    override fun success(result: ArrayList<Record>) {
        super.success(result)
        baseAdapter.addAll(result)
    }

    companion object {
        const val ACTIVE = "active"
        fun newInstance(param: String?): RssFragment {
            return newInstance(param, false)
        }

        fun newInstance(param: String?, active: Boolean): RssFragment {
            val fragment = RssFragment()
            val bundle = Bundle()
            val url = if (param == null) {
                KisssubUrl.RSS_BASE
            } else {
                KisssubUrl.RSS_BASE + "-" + param
            }
            bundle.putString(URL, url)
            bundle.putString(SUFFIX, ".xml")
            bundle.putBoolean(ACTIVE, active)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun loadDataEnd() {
        isLoaded = true
        refreshLayout?.finishRefresh()
    }

    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask
    override fun isShowCircularReveal() = false
    override fun getContainerViewId(): Int = R.layout.fragment_default
    private val active: Boolean  by lazy {
        arguments?.getBoolean(ACTIVE, false) ?: false
    }
    private var isLoaded: Boolean = false
    override fun onVisible() {
        if (!isLoaded) {
            refreshLayout?.autoRefresh()
        }
    }

    private val baseAdapter: RssAdapter by lazy { RssAdapter() }
}
