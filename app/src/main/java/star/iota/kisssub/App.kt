package star.iota.kisssub

import android.app.Application
import android.content.Context
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheEntity
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.cookie.CookieJarImpl
import com.lzy.okgo.cookie.store.DBCookieStore
import com.lzy.okgo.https.HttpsUtils
import com.lzy.okgo.model.HttpHeaders
import com.zzhoujay.richtext.RichText
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class App : Application() {

    companion object {
        val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36"
        fun makeOkHttpClient(context: Context): OkHttpClient {
            val builder = OkHttpClient.Builder()
//            val loggingInterceptor = HttpLoggingInterceptor("OkGo")
//            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY)
//            loggingInterceptor.setColorLevel(Level.INFO)
//            builder.addInterceptor(loggingInterceptor)
            builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            builder.cookieJar(CookieJarImpl(DBCookieStore(context)))
            val sslParams = HttpsUtils.getSslSocketFactory()
            builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
            return builder.build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        RichText.initCacheDir(this)
        initOkGo()
    }


    private fun initOkGo() {
        val headers = HttpHeaders()
        headers.put("User-Agent", USER_AGENT)
        OkGo.getInstance().init(this)
                .setOkHttpClient(makeOkHttpClient(this))
                .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                .setRetryCount(3)
                .addCommonHeaders(headers)
    }
}