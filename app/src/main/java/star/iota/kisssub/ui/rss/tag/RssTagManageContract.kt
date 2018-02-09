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

package star.iota.kisssub.ui.rss.tag

import star.iota.kisssub.base.BasePresenter
import star.iota.kisssub.room.AppDatabaseHelper
import star.iota.kisssub.room.RssTag

interface RssTagManageContract {
    interface View {
        fun success(tags: ArrayList<RssTag>)
        fun success(rssTag: RssTag)
        fun error(e: String?)
        fun noData()
    }

    interface Presenter : BasePresenter {
        fun get(helper: AppDatabaseHelper)
        fun add(helper: AppDatabaseHelper, rssTag: RssTag)
    }
}
