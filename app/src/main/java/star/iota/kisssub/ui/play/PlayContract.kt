package star.iota.kisssub.ui.play

import star.iota.kisssub.base.BasePresenter

interface PlayContract{
    interface View {
        fun success(items: ArrayList<FanBean>)
        fun error(e: String?)
        fun noData()
    }

    interface Presenter : BasePresenter {
        fun get(url: String)
    }
}