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

package star.iota.kisssub.base

import com.lzy.okgo.OkGo
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Cookie
import okhttp3.HttpUrl
import star.iota.kisssub.KisssubUrl


abstract class BasePresenter {
    abstract fun unsubscribe()
    protected open fun addCookie(url: String) {
        Single.just(url)
                .filter { it.contains(KisssubUrl.BASE) }
                .map { u ->
                    val cookieStore = OkGo.getInstance().cookieJar.cookieStore
                    val godCookies = cookieStore.getCookie(HttpUrl.parse(KisssubUrl.GOD_MODE))
                    if (godCookies != null && godCookies.size > 0) {
                        godCookies.forEach {
                            val godMode = "god_mode"
                            if (it.name().contains(godMode)) {
                                val httpUrl = getHttpUrl(u)
                                val cookie = Cookie.Builder().name(godMode).value(it.value()).domain(httpUrl.host()).build()
                                cookieStore.saveCookie(httpUrl, cookie)
                            }
                        }
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe()
    }

    private fun getHttpUrl(url: String) = HttpUrl.parse(url) ?: HttpUrl.parse(KisssubUrl.BASE)!!
}
