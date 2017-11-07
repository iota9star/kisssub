package star.iota.kisssub.ui.play

import com.lzy.okgo.OkGo
import com.lzy.okgo.convert.StringConvert
import com.lzy.okgo.model.Response
import com.lzy.okrx2.adapter.ObservableResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup


class PlayPresenter(private val view: PlayContract.View) : PlayContract.Presenter {
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

    private fun deal(resp: Response<String>): ArrayList<FanBean> {
        val elements = Jsoup.parse(resp.body())?.select("#bgm-table > dl")
        val items = ArrayList<FanBean>()
        elements?.forEach {
            val week = it?.select("dt")?.first()?.text()
            val w = FanBean()
            w.type = FanBean.WEEK
            w.title = week
            items.add(w)
            it?.select("dd > a")?.forEach {
                val title = it?.text()
                val tip = it?.attr("data-balloon")
                val fan = FanBean()
                fan.tip = tip
                fan.title = title
                fan.type = FanBean.FAN
                items.add(fan)
            }
        }
        return items
    }


    companion object {
        private val compositeDisposable = CompositeDisposable()
    }

    override fun unsubscribe() {
        compositeDisposable.clear()
    }
}