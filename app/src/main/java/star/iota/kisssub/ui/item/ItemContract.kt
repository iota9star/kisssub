package star.iota.kisssub.ui.item

import star.iota.kisssub.base.BasePresenter
import star.iota.kisssub.room.Record

interface ItemContract{
    interface View {
        fun success(items: ArrayList<Record>)
        fun error(e: String?)
        fun noData()
    }

    interface Presenter : BasePresenter {
        fun get(url: String)
    }
}