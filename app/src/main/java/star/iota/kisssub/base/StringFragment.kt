package star.iota.kisssub.base

import com.scwang.smartrefresh.layout.SmartRefreshLayout
import star.iota.kisssub.widget.M


abstract class StringFragment<out P : StringPresenter<T>, T> : BaseFragment(), StringContract.View<T> {

    protected abstract fun getRefreshLayout(): SmartRefreshLayout?
    protected abstract fun getStringPresenter(): P
    protected open fun doOther(){}
    private val presenter: P by lazy { getStringPresenter() }
    private var isLoading = false
    private val url: String by lazy {
        arguments?.getString(URL, "").toString()
    }

    companion object {
        const val TITLE = "title"
        const val URL = "url"
    }

    private fun initBase() {
        val title = arguments?.getString(TITLE, "")
        if (title.toString().isNotBlank()) {
            setToolbarTitle(title)
        }
    }

    final override fun doSome() {
        initBase()
        initRefreshLayout()
        doOther()
    }

    private fun initRefreshLayout() {
        val layout = getRefreshLayout()
        layout?.autoRefresh()
        layout?.isEnableLoadMore = false
        layout?.setOnRefreshListener {
            if (!checkIsLoading()) {
                onRefresh()
                presenter.get(url)
            }
        }
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
        getRefreshLayout()?.finishRefresh()
    }

    final override fun error(e: String?) {
        isLoading = false
        getRefreshLayout()?.finishRefresh()
        M.create(activity().applicationContext, "请求数据时发生错误：$e")
    }

    final override fun noData() {
        isLoading = false
        getRefreshLayout()?.finishRefresh()
        M.create(activity().applicationContext, "没有获得数据，请稍后重试")
    }

    final override fun isCache() {
        isLoading = false
        getRefreshLayout()?.finishRefresh()
        M.create(activity().applicationContext, "请注意，以上数据来自缓存")
    }

    override fun onDestroyView() {
        presenter.unsubscribe()
        super.onDestroyView()
    }
}
