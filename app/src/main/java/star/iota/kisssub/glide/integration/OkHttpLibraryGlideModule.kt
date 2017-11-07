package star.iota.kisssub.glide.integration

import android.content.Context

import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.LibraryGlideModule
import star.iota.kisssub.App

import java.io.InputStream

@GlideModule
class OkHttpLibraryGlideModule : LibraryGlideModule() {
    override fun registerComponents(context: Context?, glide: Glide?, registry: Registry?) {
        registry!!.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(App.makeOkHttpClient(context!!)))
    }
}
