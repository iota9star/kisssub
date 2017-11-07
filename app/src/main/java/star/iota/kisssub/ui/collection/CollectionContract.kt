package star.iota.kisssub.ui.collection

import star.iota.kisssub.base.BasePresenter
import star.iota.kisssub.room.AppDatabaseHelper
import star.iota.kisssub.room.Record

interface CollectionContract {
    interface View {
        fun success(items: ArrayList<Record>)
        fun error(e: String?)
        fun noData()
    }

    interface Presenter : BasePresenter {
        fun get(helper: AppDatabaseHelper)
    }
}