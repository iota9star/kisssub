/*
 *
 *  *    Copyright 2018. iota9star
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package star.iota.kisssub.ui.details

import com.lzy.okgo.OkGo
import com.lzy.okgo.convert.StringConvert
import com.lzy.okgo.model.Response
import com.lzy.okrx2.adapter.ObservableResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import star.iota.kisssub.KisssubUrl

class DetailsPresenter(private val view: DetailsContract.View) : DetailsContract.Presenter() {
    override fun get(url: String) {
        addCookie(url)
        compositeDisposable.add(
                OkGo.get<String>(url)
                        .converter(StringConvert())
                        .adapt(ObservableResponse<String>())
                        .map { deal(it) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it == null) {
                                view.noData()
                            } else {
                                view.success(it)
                            }
                        }, {
                            view.error(it?.message)
                        })
        )
    }

    private fun deal(resp: Response<String>): DetailsBean? {
        val doc = Jsoup.parse(resp.body())?.select("#btm > div.main > div.slayout > div > div.c2")
        val bean = DetailsBean()
        val details = doc?.select("div:nth-child(1) > div.intro")?.html()?.replace("<br>", "")
        val tags = ArrayList<String>()
        doc?.select("div:nth-child(4) > div > a")?.forEach {
            tags.add(it.text())
        }
        val magnet = doc?.select("#magnet")?.attr("href")
        val torrent = KisssubUrl.BASE + doc?.select("#download")?.attr("href")

        val desc = doc?.select("div:nth-child(6) > h2 > span.right.text_normal")?.text()
        doc?.select("div:nth-child(6) > div.torrent_files")?.select("img")?.remove()
        val tree = doc?.select("div:nth-child(6) > div.torrent_files")?.html()
        bean.desc = desc
        bean.details = details
        bean.tree = tree
        bean.magnet = magnet
        bean.tags = tags
        bean.torrent = torrent
        return bean
    }

    companion object {
        private val compositeDisposable = CompositeDisposable()
    }

    override fun unsubscribe() {
        compositeDisposable.clear()
    }
}
