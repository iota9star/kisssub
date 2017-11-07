package star.iota.kisssub.ui.tags

import com.lzy.okgo.OkGo
import com.lzy.okgo.convert.StringConvert
import com.lzy.okgo.model.Response
import com.lzy.okrx2.adapter.ObservableResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup


class TagsPresenter(private val view: TagsContract.View) : TagsContract.Presenter {
    override fun get(url: String) {
        compositeDisposable.add(
                OkGo.get<String>(url)
                        .converter(StringConvert())
                        .adapt(ObservableResponse<String>())
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .map { deal(it) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it == null || it.isEmpty()) {
                                view.noData()
                            } else {
                                view.success(it)
                            }
                        }, {
                            view.error(it?.message)
                        })
        )
    }

    private fun deal(resp: Response<String>): ArrayList<String> {
        val items = Jsoup.parse(resp.body())?.select("div.main div.box table tr td > span.tag > a")
        val list = ArrayList<String>()
        items?.forEach {
            list.add(it.text())
        }
        return list
    }


    companion object {
        private val compositeDisposable = CompositeDisposable()
    }

    override fun unsubscribe() {
        compositeDisposable.clear()
    }
}