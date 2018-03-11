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

package star.iota.kisssub.ui.history

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.fragment_default.*
import star.iota.kisssub.KisssubUrl
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseAdapter
import star.iota.kisssub.base.StringListFragment
import star.iota.kisssub.base.StringPresenter


class HistoryFragment : StringListFragment<StringPresenter<ArrayList<HistoryBean>>, ArrayList<HistoryBean>, HistoryBean>() {

    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask
    override fun getContainerViewId(): Int = R.layout.fragment_default
    override fun getRefreshLayout(): SmartRefreshLayout? = refreshLayout
    override fun getRecyclerView(): RecyclerView? = recyclerView
    override fun getAdapter(): BaseAdapter<HistoryBean> = baseAdapter
    override fun getStringPresenter(): StringPresenter<ArrayList<HistoryBean>> = HistoryPresenter(this)
    override fun isStableUrl() = true
    override fun getStableUrl() = KisssubUrl.PLAY

    private val baseAdapter: BaseAdapter<HistoryBean> by lazy { HistoryAdapter() }

    override fun setupRefreshLayout(refreshLayout: SmartRefreshLayout?) {
        refreshLayout?.autoRefresh()
        refreshLayout?.isEnableLoadMore = false
    }

    override fun initTitle() {
        setToolbarTitle(getString(R.string.menu_history))
    }

    override fun success(result: ArrayList<HistoryBean>) {
        super.success(result)
        if (result.isNotEmpty()) {
            result.removeAt(0)
        }
        baseAdapter.addAll(result)
    }

    companion object {
        fun newInstance() = HistoryFragment()
    }

    override fun loadDataEnd() {
        refreshLayout?.finishRefresh()
    }
}
