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
import com.lzy.okgo.convert.StringConvert
import com.lzy.okgo.model.Response
import com.lzy.okrx2.adapter.ObservableResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Cookie
import okhttp3.HttpUrl
import star.iota.kisssub.KisssubUrl

abstract class StringPresenter<out T>(private val view: StringContract.View<T>) : StringContract.Presenter {
    private fun addCookie(url: String) {
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

    final override fun get(url: String) {
        addCookie(url)
        compositeDisposable.add(
                OkGo.get<String>(url)
                        .converter(StringConvert())
                        .adapt(ObservableResponse<String>())
                        .map {
                            if (it.isFromCache) {
                                view.isCache()
                            }
                            deal(it)
                        }
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

    abstract fun deal(resp: Response<String>): T?

    companion object {
        private val compositeDisposable = CompositeDisposable()
    }

    override fun unsubscribe() {
        compositeDisposable.clear()
    }
}
