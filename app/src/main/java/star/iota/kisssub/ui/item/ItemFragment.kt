/*
 *
 *  *    Copyright 2017. iota9star
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

package star.iota.kisssub.ui.item

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.footer.BallPulseView
import kotlinx.android.synthetic.main.fragment_default.*
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.helper.ThemeHelper
import star.iota.kisssub.room.Record
import star.iota.kisssub.widget.MessageBar

class ItemFragment : BaseFragment(), ItemContract.View {
    override fun success(items: ArrayList<Record>) {
        end(false)
        adapter.addAll(items)
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
        val TITLE = "title"
        val URL = "url"
        val SUFFIX = "suffix"

        fun newInstance(title: String, url: String): ItemFragment {
            val fragment = ItemFragment()
            val bundle = Bundle()
            bundle.putString(URL, url.replace("1.html", ""))
            bundle.putString(SUFFIX, ".html")
            bundle.putString(TITLE, title)
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun end(error: Boolean) {
        isLoading = false
        if (isRefresh) {
            page = 2
            refreshLayout.finishRefreshing()
        } else {
            if (!error) page++
            refreshLayout.finishLoadmore()
        }
    }

    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask
    override fun getContainerViewId(): Int = R.layout.fragment_default

    override fun doSome() {
        initBase()
        initPresenter()
        initRecyclerView()
        initRefreshLayout()
    }

    private lateinit var url: String
    private lateinit var suffix: String
    private var page = 1

    private fun initBase() {
        setToolbarTitle(arguments!!.getString(TITLE, getString(R.string.app_name)))
        url = arguments!!.getString(URL)
        suffix = arguments!!.getString(SUFFIX, "")
    }

    private lateinit var presenter: ItemPresenter
    private fun initPresenter() {
        presenter = ItemPresenter(this)
    }

    private var isLoading = false
    private var isRefresh = false
    private fun initRefreshLayout() {
        val footer = BallPulseView(context!!)
        footer.setNormalColor(ThemeHelper.getAccentColor(context!!))
        footer.setAnimatingColor(ThemeHelper.getAccentColor(context!!))
        refreshLayout.setBottomView(footer)
        refreshLayout.startRefresh()
        refreshLayout.setAutoLoadMore(true)
        refreshLayout.setOnRefreshListener(object : RefreshListenerAdapter() {
            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                if (!checkIsLoading()) {
                    isRefresh = false
                    isLoading = true
                    presenter.get(url + page + suffix)
                }
            }

            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                if (!checkIsLoading()) {
                    isRefresh = true
                    isLoading = true
                    adapter.clear()
                    presenter.get(url + "1" + suffix)
                }
            }
        })
    }

    private fun checkIsLoading(): Boolean = if (isLoading) {
        MessageBar.create(context!!, "数据正在加载中，请等待...")
        true
    } else {
        false
    }

    private lateinit var adapter: ItemAdapter
    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
//        recyclerView.itemAnimator = FadeInUpAnimator()
        adapter = ItemAdapter()
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unsubscribe()
    }

}