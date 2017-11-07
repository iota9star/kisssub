package star.iota.kisssub.ui.collection

import android.support.v7.widget.LinearLayoutManager
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.fragment_default.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.room.AppDatabaseHelper
import star.iota.kisssub.room.Record
import star.iota.kisssub.widget.MessageBar

class CollectionFragment : BaseFragment(), CollectionContract.View {
    override fun success(items: ArrayList<Record>) {
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
        fun newInstance(): CollectionFragment {
            return CollectionFragment()
        }
    }

    private fun end() {
        isLoading = false
        refreshLayout.finishRefreshing()
    }


    override fun getContainerViewId(): Int = R.layout.fragment_default

    override fun doSome() {
        setToolbarTitle(context!!.getString(R.string.menu_favorite))
        initPresenter()
        initRecyclerView()
        initRefreshLayout()
    }

    private lateinit var presenter: CollectionPresenter
    private fun initPresenter() {
        presenter = CollectionPresenter(this)
    }

    private var isLoading = false
    private fun initRefreshLayout() {
        refreshLayout.startRefresh()
        refreshLayout.setEnableLoadmore(false)
        refreshLayout.setOnRefreshListener(object : RefreshListenerAdapter() {
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                if (!checkIsLoading()) {
                    isLoading = true
                    adapter.clear()
                    presenter.get(AppDatabaseHelper.getInstance(context!!))
                }
            }
        })
    }

    private fun checkIsLoading(): Boolean {
        if (isLoading) {
            MessageBar.create(context!!, "数据正在加载中，请等待...")
            return true
        }
        return false
    }

    private lateinit var adapter: CollectionAdapter
    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        recyclerView.itemAnimator = LandingAnimator()
        adapter = CollectionAdapter()
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unsubscribe()
    }

}