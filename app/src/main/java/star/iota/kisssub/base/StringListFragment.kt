package star.iota.kisssub.base

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import jp.wasabeef.recyclerview.animators.LandingAnimator
import star.iota.kisssub.widget.M


abstract class StringListFragment<out P : StringPresenter<T>, T, E> : BaseFragment(), StringContract.View<T> {

    protected abstract fun getRefreshLayout(): SmartRefreshLayout?
    protected abstract fun getRecyclerView(): RecyclerView?
    protected open fun getLayoutManger(): RecyclerView.LayoutManager = LinearLayoutManager(activity(), LinearLayoutManager.VERTICAL, false)
    protected open fun getItemAnimator(): RecyclerView.ItemAnimator = LandingAnimator()
    protected abstract fun getAdapter(): BaseAdapter<E>
    protected abstract fun setupRefreshLayout(refreshLayout: SmartRefreshLayout?)
    protected abstract fun getStringPresenter(): P
    protected open fun doOther() {}
    protected fun isRefresh() = isRefresh
    private val presenter: P by lazy { getStringPresenter() }

    private val baseAdapter: BaseAdapter<E> by lazy { getAdapter() }

    private var isLoading = false
    private var isRefresh = false

    private val url: String by lazy {
        arguments?.getString(URL, "").toString()
    }
    private val suffix: String by lazy {
        arguments?.getString(SUFFIX, "").toString()
    }
    private val is_stable_url: Boolean by lazy {
        if (arguments != null) {
            arguments!!.getBoolean(IS_STABLE_URL, false)
        } else {
            false
        }
    }
    private var page = 1

    protected open fun initTitle() {
        val title = arguments?.getString(TITLE, "")
        if (title.toString().isNotBlank()) {
            setToolbarTitle(title)
        }
    }

    companion object {
        const val TITLE = "title"
        const val URL = "url"
        const val SUFFIX = "suffix"
        const val IS_STABLE_URL = "is_stable_url"
    }

    private fun createUrl() = if (isStableUrl())
        getStableUrl()
    else {
        if (is_stable_url) {
            url
        } else {
            url + page + suffix
        }
    }

    protected open fun isStableUrl() = false
    protected open fun getStableUrl(): String = ""

    protected open fun setPage(page: Int) {
        this.page = page
    }

    protected fun pagePlus() {
        this.page++
    }

    final override fun doSome() {
        initTitle()
        initRecyclerView()
        initRefreshLayout()
        doOther()
    }

    private fun initRecyclerView() {
        val view = getRecyclerView()
        view?.layoutManager = getLayoutManger()
        view?.itemAnimator = getItemAnimator()
        view?.adapter = baseAdapter
    }

    private fun initRefreshLayout() {
        val layout = getRefreshLayout()
        setupRefreshLayout(layout)
        layout?.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                if (!checkIsLoading()) {
                    isRefresh = true
                    isLoading = true
                    baseAdapter.clear()
                    page = 1
                    onRefresh()
                    presenter.get(createUrl())
                }
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                if (!checkIsLoading()) {
                    isRefresh = false
                    isLoading = true
                    onLoadMore()
                    presenter.get(createUrl())
                }
            }
        })
    }

    protected open fun onLoadMore() {

    }

    protected open fun onRefresh() {

    }


    private fun checkIsLoading(): Boolean = if (isLoading) {
        M.create(activity().applicationContext, "数据正在加载中，请等待...")
        true
    } else {
        false
    }

    override fun success(result: T) {
        isLoading = false
        loadDataEnd()
    }

    final override fun error(e: String?) {
        isLoading = false
        loadDataEnd()
        M.create(activity().applicationContext, "请求数据时发生错误：$e")
    }

    final override fun noData() {
        isLoading = false
        loadDataEnd()
        M.create(activity().applicationContext, "没有获得数据，请稍后重试")
    }

    final override fun isCache() {
        isLoading = false
        loadDataEnd()
        M.create(activity().applicationContext, "请注意，以上数据来自缓存")
    }

    protected open fun loadDataEnd() {

    }

    override fun onDestroyView() {
        presenter.unsubscribe()
        super.onDestroyView()
    }
}
