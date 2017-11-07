package star.iota.kisssub.ui.subs

import star.iota.kisssub.base.BasePresenter

interface SubsContract {
    interface View {
        fun success(items: ArrayList<String>)
        fun error(e: String?)
        fun noData()
    }

    interface Presenter : BasePresenter {
        fun get(url: String)
    }
}