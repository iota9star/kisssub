package star.iota.kisssub.ui.history

import star.iota.kisssub.base.BasePresenter

interface HistoryContract {
    interface View {
        fun success(items: ArrayList<HistoryBean>)
        fun error(e: String?)
        fun noData()
    }

    interface Presenter : BasePresenter {
        fun get(url: String)
    }
}