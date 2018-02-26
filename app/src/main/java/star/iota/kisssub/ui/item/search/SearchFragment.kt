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
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.fragment_search.*
import star.iota.kisssub.KisssubUrl
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.helper.ThemeHelper
import star.iota.kisssub.ui.item.ItemAdapter
import star.iota.kisssub.widget.MessageBar

class SearchFragment : BaseFragment(), SearchContract.View, FragmentBackHandler {

    override fun onBackPressed() =
            if (recyclerViewFilters.visibility == View.GONE) {
                false
            } else {
                recyclerViewFilters.visibility = View.GONE
                true
            }

    override fun success(result: SearchBean) {
        end(false)
        recordAdapter.addAll(result.records!!)
        if (result.filters?.isNotEmpty()!!) {
            filterAdapter.addAll(result.filters!!)
        }
    }

    override fun error(e: String?) {
        end(true)
        MessageBar.create(context!!, e)
    }

    override fun noData() {
        end(true)
        MessageBar.create(context!!, "没有获得数据")
    }

    companion object {
        const val TITLE = "title"
        const val URL = "url"
        const val SUFFIX = "suffix"
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
            bundle.putString(URL, url + "&page=")
            bundle.putString(SUFFIX, suffix)
            bundle.putString(TITLE, null)
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun end(error: Boolean) {
        isLoading = false
        if (isRefresh) {
            page = 2
            refreshLayout?.finishRefresh()
        } else {
            if (!error) page++
            refreshLayout?.finishLoadmore()
        }
    }

    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask
    override fun getContainerViewId(): Int = R.layout.fragment_search

    override fun doSome() {
        initBase()
        initPresenter()
        initRecyclerView()
        initRefreshLayout()
        initFilter()
    }

    private lateinit var filterAdapter: FilterAdapter
    private fun initFilter() {
        imageViewArrow?.setColorFilter(ThemeHelper.getAccentColor(context!!))
        linearLayoutFilter?.setOnClickListener {
            val color = ThemeHelper.getAccentColor(context!!)
            if (recyclerViewFilters?.visibility == View.VISIBLE) {
                recyclerViewFilters?.visibility = View.GONE
                imageViewArrow?.setImageResource(R.drawable.ic_arrow_down)
                imageViewArrow?.setColorFilter(color)
            } else {
                recyclerViewFilters?.visibility = View.VISIBLE
                imageViewArrow?.setImageResource(R.drawable.ic_arrow_up)
                imageViewArrow?.setColorFilter(color)
            }
        }
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        recyclerViewFilters?.layoutManager = layoutManager
        recyclerView?.itemAnimator = LandingAnimator()
        filterAdapter = FilterAdapter()
        recyclerViewFilters?.adapter = filterAdapter
    }

    private lateinit var url: String
    private lateinit var suffix: String
    private var page = 1

    private fun initBase() {
        val title = arguments!!.getString(TITLE, null)
        if (!title.isNullOrBlank()) {
            setToolbarTitle(title)
        }
        url = arguments!!.getString(URL)
        suffix = arguments!!.getString(SUFFIX, "")
    }

    private lateinit var presenter: SearchPresenter
    private fun initPresenter() {
        presenter = SearchPresenter(this)
    }

    private var isLoading = false
    private var isRefresh = false
    private fun initRefreshLayout() {
        refreshLayout?.autoRefresh()
        refreshLayout?.setOnRefreshListener {
            if (!checkIsLoading()) {
                isRefresh = true
                isLoading = true
                recordAdapter.clear()
                filterAdapter.clear()
                presenter.get(url + "1" + suffix)
            }
        }
        refreshLayout?.setOnLoadmoreListener {
            if (!checkIsLoading()) {
                isRefresh = false
                isLoading = true
                filterAdapter.clear()
                presenter.get(url + page + suffix)
            }
        }
    }

    private fun checkIsLoading(): Boolean = if (isLoading) {
        MessageBar.create(context!!, "数据正在加载中，请等待...")
        true
    } else {
        false
    }

    private lateinit var recordAdapter: ItemAdapter
    private fun initRecyclerView() {
        recyclerView?.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        recyclerView?.itemAnimator = LandingAnimator()
        recordAdapter = ItemAdapter()
        recyclerView?.adapter = recordAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unsubscribe()
    }

}
