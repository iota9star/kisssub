package star.iota.kisssub.ui.anime

import star.iota.kisssub.base.BasePresenter
import star.iota.kisssub.room.Record


interface AnimeContract {

    interface View {
        fun success(items: ArrayList<Record>)
        fun error(e: String?)
        fun noData()
    }

    interface Presenter : BasePresenter {
        fun get(url: String)
    }
}
