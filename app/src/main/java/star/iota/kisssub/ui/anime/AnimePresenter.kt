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

package star.iota.kisssub.ui.anime

import com.lzy.okgo.model.Response
import org.jsoup.Jsoup
import star.iota.kisssub.KisssubUrl
import star.iota.kisssub.base.StringContract
import star.iota.kisssub.base.StringPresenter
import star.iota.kisssub.room.Record


class AnimePresenter(view: StringContract.View<ArrayList<Record>>) : StringPresenter<ArrayList<Record>>(view) {
    override fun deal(resp: Response<String>): ArrayList<Record> {
        val items = Jsoup.parse(resp.body())?.select("div.main table div.bgm_list > ul > li > div.link")
        val list = ArrayList<Record>()
        items?.forEach {
            val bean = Record()
            bean.cover = KisssubUrl.BASE + it?.select("a > img")?.attr("data-original")
            bean.url = KisssubUrl.BASE + it?.select("span > a")?.attr("href")
            bean.title = it?.select("span > a")?.text()
            list.add(bean)
        }
        return list
    }
}
