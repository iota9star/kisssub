package star.iota.kisssub.ui.item

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.fragment_default.*
import star.iota.kisssub.KisssubUrl
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
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
        fun newSearchInstance(title: String, keywords: String): ItemFragment {
            val fragment = ItemFragment()
            val bundle = Bundle()
            bundle.putString(URL, KisssubUrl.SEARCH + keywords + "&page=")
            bundle.putString(SUFFIX, "")
            bundle.putString(TITLE, title)
            fragment.arguments = bundle
            return fragment
        }

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
        recyclerView.itemAnimator = LandingAnimator()
        adapter = ItemAdapter()
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unsubscribe()
    }

}