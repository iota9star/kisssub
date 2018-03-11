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

package star.iota.kisssub.ui.item.search

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.fragment_search.*
import star.iota.kisssub.KisssubUrl
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseAdapter
import star.iota.kisssub.base.StringListFragment
import star.iota.kisssub.base.StringPresenter
import star.iota.kisssub.room.Record
import star.iota.kisssub.ui.item.ItemAdapter

class SearchFragment : StringListFragment<StringPresenter<SearchBean>, SearchBean, Record>(), FragmentBackHandler {
    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask
    override fun getContainerViewId(): Int = R.layout.fragment_search
    override fun getRefreshLayout(): SmartRefreshLayout? = refreshLayout
    override fun getRecyclerView(): RecyclerView? = recyclerView
    override fun getAdapter(): BaseAdapter<Record> = recordAdapter
    override fun setupRefreshLayout(refreshLayout: SmartRefreshLayout?) {
        refreshLayout?.autoRefresh()
    }

    override fun getStringPresenter(): StringPresenter<SearchBean> = SearchPresenter(this)
    override fun onBackPressed() = if (recyclerViewFilters.visibility == View.GONE) {
        false
    } else {
        recyclerViewFilters.visibility = View.GONE
        true
    }

    private val recordAdapter: ItemAdapter by lazy { ItemAdapter() }

    override fun success(result: SearchBean) {
        super.success(result)
        recordAdapter.addAll(result.records)
        filterAdapter.addAll(result.filters)
    }

    companion object {
        fun newInstance(title: String, keywords: String, suffix: String): SearchFragment {
            val fragment = SearchFragment()
            val bundle = Bundle()
            bundle.putString(URL, KisssubUrl.SEARCH + keywords + "&page=")
            bundle.putString(SUFFIX, suffix)
            bundle.putString(TITLE, title)
            fragment.arguments = bundle
            return fragment
        }

        fun newFilterInstance(url: String, suffix: String): SearchFragment {
            val fragment = SearchFragment()
            val bundle = Bundle()
            bundle.putString(URL, "$url&page=")
            bundle.putString(SUFFIX, suffix)
            bundle.putString(TITLE, null)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun loadDataEnd() {
        if (isRefresh()) {
            setPage(2)
            refreshLayout?.finishRefresh()
        } else {
            pagePlus()
            refreshLayout?.finishLoadMore()
        }
    }


    override fun doOther() {
        initFilter()
    }

    private val filterAdapter: FilterAdapter by lazy { FilterAdapter() }
    private fun initFilter() {
        linearLayoutFilter?.setOnClickListener {
            if (recyclerViewFilters?.visibility == View.VISIBLE) {
                recyclerViewFilters?.visibility = View.GONE
                imageViewArrow?.setImageResource(R.drawable.ic_arrow_down)
            } else {
                recyclerViewFilters?.visibility = View.VISIBLE
                imageViewArrow?.setImageResource(R.drawable.ic_arrow_up)
            }
        }
        val layoutManager = FlexboxLayoutManager(activity())
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        recyclerViewFilters?.layoutManager = layoutManager
        recyclerViewFilters?.adapter = filterAdapter
    }

    override fun onLoadMore() {
        filterAdapter.clear()
    }

    override fun onRefresh() {
        filterAdapter.clear()
    }
}
