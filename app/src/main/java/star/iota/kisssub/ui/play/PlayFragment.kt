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

package star.iota.kisssub.ui.play

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.android.synthetic.main.fragment_recycler_view_p8.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.widget.MessageBar

class PlayFragment : BaseFragment(), PlayContract.View {
    override fun success(items: ArrayList<FanBean>) {
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
        const val URL = "url"
        const val TITLE = "title"
        fun newInstance(title: String, url: String): PlayFragment {
            val fragment = PlayFragment()
            val bundle = Bundle()
            bundle.putString(URL, url)
            bundle.putString(TITLE, title)
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun end() {
        isLoading = false
        refreshLayout?.finishRefresh()
    }

    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask
    override fun getContainerViewId(): Int = R.layout.fragment_recycler_view_p8

    override fun doSome() {
        initBase()
        initPresenter()
        initRecyclerView()
        initRefreshLayout()
    }

    private lateinit var url: String
    private fun initBase() {
        setToolbarTitle(arguments!!.getString(TITLE))
        url = arguments!!.getString(URL)
    }

    private lateinit var presenter: PlayPresenter
    private fun initPresenter() {
        presenter = PlayPresenter(this)
    }

    private var isLoading = false
    private fun initRefreshLayout() {
        refreshLayout?.autoRefresh()
        refreshLayout?.isEnableLoadmore = false
        refreshLayout?.setOnRefreshListener {
            if (!checkIsLoading()) {
                isLoading = true
                adapter.clear()
                presenter.get(url)
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

    private lateinit var adapter: PlayAdapter
    private fun initRecyclerView() {
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        recyclerView?.layoutManager = layoutManager
//        recyclerView?.itemAnimator = LandingAnimator()
        adapter = PlayAdapter()
        recyclerView?.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unsubscribe()
    }
}
