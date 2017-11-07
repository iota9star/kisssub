package star.iota.kisssub.ui.tags

import star.iota.kisssub.base.BasePresenter


interface TagsContract {

    interface View {
        fun success(items: ArrayList<String>)
        fun error(e: String?)
        fun noData()
    }

    interface Presenter : BasePresenter {
        fun get(url: String)
    }
}
