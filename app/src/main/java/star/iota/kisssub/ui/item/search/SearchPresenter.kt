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

package star.iota.kisssub.ui.item.search

import com.lzy.okgo.model.Response
import org.jsoup.Jsoup
import star.iota.kisssub.KisssubUrl
import star.iota.kisssub.base.StringContract
import star.iota.kisssub.base.StringPresenter
import star.iota.kisssub.room.Record


class SearchPresenter(view: StringContract.View<SearchBean>) : StringPresenter<SearchBean>(view) {

    override fun deal(resp: Response<String>): SearchBean? {
        val doc = Jsoup.parse(resp.body())
        val records = doc?.select("#data_list > tr")
        val rs = ArrayList<Record>()
        records?.forEach {
            val bean = Record()
            bean.type = Record.NO_IMAGE
            bean.date = it?.select("td:nth-child(1)")?.text()
            bean.category = it?.select("td:nth-child(2) > a")?.text()
            val title = it?.select("td:nth-child(3) > a")?.text()
            bean.title = ("/" + title?.replace(Regex("]\\s*\\[|\\[|]|】\\s*【|】|【"), "/") + "/").replace(Regex("/\\s*/+"), "/")
            val tUrl = it?.select("td:nth-child(3) > a")?.attr("href")
            val hash = tUrl?.replace("show-", "")?.replace(".html", "")
            bean.magnet = "magnet:?xt=urn:btih:$hash&tr=http://open.acgtracker.com:1096/announce"
            bean.url = KisssubUrl.BASE + tUrl
            bean.size = it?.select("td:nth-child(4)")?.text()
            bean.sub = it?.select("td:nth-child(8) > a")?.text()
            rs.add(bean)
        }

        val filters = doc?.select("#btm > div.main > div:nth-child(1) > div > table > tbody > tr")
        val fs = ArrayList<FilterBean>()
        filters?.forEach {
            val header = FilterBean()
            header.title = it?.select("td:nth-child(1)")?.text()
            header.type = FilterBean.HEADER
            fs.add(header)
            val tags = it?.select("td:nth-child(2) > a")
            tags?.forEach {
                val bean = FilterBean()
                bean.type = FilterBean.TAG
                bean.title = it?.text()
                bean.url = KisssubUrl.BASE + it?.attr("href")
                fs.add(bean)
            }
        }
        val data = SearchBean()
        data.records = rs
        data.filters = fs
        return data
    }
}
