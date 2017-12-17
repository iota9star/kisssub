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

package star.iota.kisssub.ui.subs

import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_recycler_view_p8.*
import star.iota.kisssub.KisssubUrl
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.widget.MessageBar

class SubsFragment : BaseFragment(), SubsContract.View {
    override fun success(items: ArrayList<String>) {
        end()
        adapter.addAll(items)
    }

    override fun error(e: String?) {
        end()
        MessageBar.create(context!!, e)
    }

    override fun noData() {
        end()
        MessageBar.create(context!!, "没有获得数据")
    }

    companion object {
        fun newInstance(): SubsFragment = SubsFragment()
    }

    private fun end() {
        isLoading = false
        refreshLayout.finishRefresh()
    }

    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask

    override fun getContainerViewId(): Int = R.layout.fragment_recycler_view_p8

    override fun doSome() {
        setToolbarTitle(context!!.getString(R.string.menu_member))
        initPresenter()
        initRecyclerView()
        initRefreshLayout()
    }

    private lateinit var presenter: SubsPresenter
    private fun initPresenter() {
        presenter = SubsPresenter(this)
    }

    private var isLoading = false
    private fun initRefreshLayout() {
        refreshLayout.autoRefresh()
        refreshLayout.isEnableLoadmore = false
        refreshLayout.setOnRefreshListener {
            if (!checkIsLoading()) {
                isLoading = true
                adapter.clear()
                presenter.get(KisssubUrl.SUBS)
            }
        }
    }

    private fun checkIsLoading(): Boolean {
        if (isLoading) {
            MessageBar.create(context!!, "数据正在加载中，请等待...")
            return true
        }
        return false
    }

    private lateinit var adapter: SubsAdapter
    private fun initRecyclerView() {
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        recyclerView.itemAnimator = LandingAnimator()
        adapter = SubsAdapter()
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unsubscribe()
    }
}
