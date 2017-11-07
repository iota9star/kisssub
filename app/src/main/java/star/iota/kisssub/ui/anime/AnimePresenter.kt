package star.iota.kisssub.ui.anime

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


class AnimePresenter(private val view: AnimeContract.View) : AnimeContract.Presenter {
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
        val items = Jsoup.parse(resp.body())?.select("div.main table div.bgm_list > ul > li > div.link")
        val list = ArrayList<Record>()
        items?.forEach {
            val bean = Record()
            bean.type = Record.FAN
            bean.cover = KisssubUrl.BASE + it?.select("a > img")?.attr("data-original")
            bean.url = KisssubUrl.BASE + it?.select("span > a")?.attr("href")
            bean.title = it?.select("span > a")?.text()
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