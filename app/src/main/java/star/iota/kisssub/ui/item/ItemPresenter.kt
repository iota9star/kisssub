package star.iota.kisssub.ui.item

import com.lzy.okgo.OkGo
import com.lzy.okgo.convert.StringConvert
import com.lzy.okgo.model.Response
import com.lzy.okrx2.adapter.ObservableResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import star.iota.kisssub.KisssubUrl
import star.iota.kisssub.room.Record


class ItemPresenter(private val view: ItemContract.View) : ItemContract.Presenter {
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

    private fun deal(resp: Response<String>): ArrayList<Record> {
        val items = Jsoup.parse(resp.body())?.select("#data_list > tr")
        val list = ArrayList<Record>()
        items?.forEach {
            val bean = Record()
            bean.type = Record.NO_IMAGE
            bean.date = it?.select("td:nth-child(1)")?.text()
            bean.category = it?.select("td:nth-child(2) > a")?.text()
            bean.title = it?.select("td:nth-child(3) > a")?.text()
            bean.url = KisssubUrl.BASE + it?.select("td:nth-child(3) > a")?.attr("href")
            bean.size = it?.select("td:nth-child(4)")?.text()
            bean.sub = it?.select("td:nth-child(8) > a")?.text()
            list.add(bean)
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