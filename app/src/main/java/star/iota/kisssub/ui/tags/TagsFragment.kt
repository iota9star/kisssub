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

package star.iota.kisssub.ui.tags

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.fragment_recycler_view_p8.*
import star.iota.kisssub.KisssubUrl
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseAdapter
import star.iota.kisssub.base.StringListFragment
import star.iota.kisssub.base.StringPresenter


class TagsFragment : StringListFragment<StringPresenter<ArrayList<String>>, ArrayList<String>, String>() {
    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask
    override fun getContainerViewId(): Int = R.layout.fragment_recycler_view_p8
    override fun getRefreshLayout(): SmartRefreshLayout? = refreshLayout
    override fun getRecyclerView(): RecyclerView? = recyclerView
    override fun isStableUrl() = true
    override fun getStableUrl() = KisssubUrl.TAGS
    override fun getAdapter(): BaseAdapter<String> = baseAdapter
    override fun getStringPresenter(): StringPresenter<ArrayList<String>> = TagsPresenter(this)

    override fun setupRefreshLayout(refreshLayout: SmartRefreshLayout?) {
        refreshLayout?.autoRefresh()
        refreshLayout?.isEnableLoadMore = false
    }

    override fun initTitle() {
        setToolbarTitle(getString(R.string.menu_tags))
    }

    override fun success(result: ArrayList<String>) {
        super.success(result)
        baseAdapter.addAll(result)
    }

    companion object {
        fun newInstance() = TagsFragment()
    }

    override fun loadDataEnd() {
        refreshLayout?.finishRefresh()
    }

    private val baseAdapter: TagsAdapter by lazy { TagsAdapter() }
    override fun getLayoutManger(): RecyclerView.LayoutManager {
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        recyclerView?.layoutManager = layoutManager
        return layoutManager
    }
}
