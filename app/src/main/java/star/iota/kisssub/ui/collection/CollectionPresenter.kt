package star.iota.kisssub.ui.collection

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import star.iota.kisssub.room.AppDatabaseHelper
import star.iota.kisssub.room.Record

class CollectionPresenter(private val view: CollectionContract.View) : CollectionContract.Presenter {
    override fun get(helper: AppDatabaseHelper) {
        compositeDisposable.add(
                Single.just(helper)
                        .map { it.all() }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it == null || it.isEmpty()) {
                                view.noData()
                            } else {
                                view.success(it as ArrayList<Record>)
                            }
                        }, { view.error(it?.message) })
        )
    }


    companion object {
        private val compositeDisposable = CompositeDisposable()
    }

    override fun unsubscribe() {
        compositeDisposable.clear()
    }
}