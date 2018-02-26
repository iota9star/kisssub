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

package star.iota.kisssub.ui.about

import com.lzy.okgo.OkGo
import com.lzy.okgo.convert.StringConvert
import com.lzy.okrx2.adapter.ObservableResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.HttpUrl
import star.iota.kisssub.KisssubUrl


class GodModePresenter(private val view: GodModeContract.View) : GodModeContract.Presenter() {
    override fun get(url: String, code: String) {
        compositeDisposable.add(
                OkGo.post<String>(url)
                        .params("op", "enable")
                        .params("access_token", code)
                        .converter(StringConvert())
                        .adapt(ObservableResponse<String>())
                        .subscribeOn(Schedulers.io())
                        .map { deal(code) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            view.isActivated(it)
                        }, {
                            view.other(it?.message)
                        })
        )
    }

    private fun deal(code: String): Boolean {
        val cookieStore = OkGo.getInstance().cookieJar.cookieStore
        val httpUrl = HttpUrl.parse(KisssubUrl.GOD_MODE)
        val cookies = cookieStore.getCookie(httpUrl)
        val pattern = "god_mode\\s*=\\s*$code"
        return cookies.toString().contains(Regex(pattern))
    }


    companion object {
        private val compositeDisposable = CompositeDisposable()
    }

    override fun unsubscribe() {
        compositeDisposable.clear()
    }
}
