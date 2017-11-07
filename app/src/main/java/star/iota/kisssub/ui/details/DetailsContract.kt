package star.iota.kisssub.ui.details

import star.iota.kisssub.base.BasePresenter

interface DetailsContract {
    interface View {
        fun success(bean: DetailsBean)
        fun error(e: String?)
        fun noData()
    }

    interface Presenter : BasePresenter {
        fun get(url: String)
    }
}