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

package star.iota.kisssub.ui.anime

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.ImageView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.fragment_recycler_view_p4.*
import star.iota.kisssub.KisssubUrl
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseAdapter
import star.iota.kisssub.base.StringContract
import star.iota.kisssub.base.StringListFragment
import star.iota.kisssub.base.StringPresenter
import star.iota.kisssub.room.Record


class AnimeFragment : StringListFragment<StringPresenter<ArrayList<Record>>, ArrayList<Record>, Record>(), StringContract.View<ArrayList<Record>> {
    override fun getLayoutManger(): RecyclerView.LayoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
    override fun getRefreshLayout(): SmartRefreshLayout? = refreshLayout
    override fun getRecyclerView(): RecyclerView? = recyclerView
    override fun getAdapter(): BaseAdapter<Record> = baseAdapter
    override fun getStringPresenter(): StringPresenter<ArrayList<Record>> = AnimePresenter(this)
    override fun getContainerViewId(): Int = R.layout.fragment_recycler_view_p4
    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask
    override fun isStableUrl() = true
    override fun getStableUrl() = KisssubUrl.BGMLIST
    private val baseAdapter: AnimeAdapter by lazy { AnimeAdapter() }
    override fun setupRefreshLayout(refreshLayout: SmartRefreshLayout?) {
        refreshLayout?.autoRefresh()
        refreshLayout?.isEnableLoadMore = false
    }

    override fun initTitle() {
        setToolbarTitle(getString(R.string.menu_fans))
    }

    override fun success(result: ArrayList<Record>) {
        super.success(result)
        baseAdapter.addAll(result)
    }

    companion object {
        fun newInstance() = AnimeFragment()
    }

    override fun loadDataEnd() {
        refreshLayout?.finishRefresh()
    }

}
