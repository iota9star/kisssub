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

package star.iota.kisssub.ui.play

import com.lzy.okgo.model.Response
import org.jsoup.Jsoup
import star.iota.kisssub.base.StringContract
import star.iota.kisssub.base.StringPresenter


class PlayPresenter(view: StringContract.View<ArrayList<FanBean>>) : StringPresenter<ArrayList<FanBean>>(view) {
    override fun deal(resp: Response<String>): ArrayList<FanBean> {
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
}
