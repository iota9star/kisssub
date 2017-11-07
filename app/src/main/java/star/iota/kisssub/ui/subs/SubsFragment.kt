package star.iota.kisssub.ui.subs

import android.support.v7.widget.StaggeredGridLayoutManager
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.fragment_recycler_view_p4.*
import star.iota.kisssub.KisssubUrl
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.base.SGSpacingItemDecoration
import star.iota.kisssub.utils.DisplayUtils
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
        fun newInstance(): SubsFragment {
            return SubsFragment()
        }
    }

    private fun end() {
        isLoading = false
        refreshLayout.finishRefreshing()
    }


    override fun getContainerViewId(): Int = R.layout.fragment_recycler_view_p4

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
        refreshLayout.startRefresh()
        refreshLayout.setEnableLoadmore(false)
        refreshLayout.setOnRefreshListener(object :RefreshListenerAdapter(){
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                if (!checkIsLoading()) {
                    isLoading = true
                    adapter.clear()
                    presenter.get(KisssubUrl.SUBS)
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

    private lateinit var adapter: SubsAdapter
    private fun initRecyclerView() {
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator = LandingAnimator()
        recyclerView.addItemDecoration(SGSpacingItemDecoration(2, DisplayUtils.dp2px(context!!, 16f)))
        adapter = SubsAdapter()
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unsubscribe()
    }
}