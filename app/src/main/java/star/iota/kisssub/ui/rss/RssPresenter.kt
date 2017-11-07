package star.iota.kisssub.ui.rss

import com.lzy.okgo.OkGo
import com.lzy.okgo.convert.StringConvert
import com.lzy.okgo.model.Response
import com.lzy.okrx2.adapter.ObservableResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import star.iota.kisssub.room.Record

class RssPresenter(private val view: RssContract.View) : RssContract.Presenter {
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
        val items = Jsoup.parse(resp.body())?.select("item")
        val list = ArrayList<Record>()
        items?.forEach {
            val bean = Record()
            bean.date = it?.select("pubdate")?.text()?.replace("+0800", "")
            bean.category = it?.select("category")?.text()
            bean.title = it?.select("title")?.text()?.replace("<![CDATA[", "")?.replace("]]>", "")
            bean.magnet = it?.select("enclosure")?.attr("url")
            bean.url = it?.select("guid")?.text()
            bean.sub = it?.select("author")?.text()
            val desc = it?.select("description")?.html()?.replace("<![CDATA[", "")?.replace("]]>", "")?.replace("&gt;", ">")?.replace("&lt;", "<")
            bean.desc = desc
            bean.cover = Jsoup.parseBodyFragment(desc)?.select("img")?.first()?.attr("src")
            if (bean.cover.isNullOrBlank()) {
                bean.type = Record.NO_IMAGE
            } else {
                bean.type = Record.WITH_IMAGE
            }
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