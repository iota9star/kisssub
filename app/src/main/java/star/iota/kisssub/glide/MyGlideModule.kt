package star.iota.kisssub.glide

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class MyGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context?, builder: GlideBuilder?) {
        val diskCacheSizeBytes = Integer.MAX_VALUE
        builder!!.setDiskCache(InternalCacheDiskCacheFactory(context, diskCacheSizeBytes))
    }

    override fun isManifestParsingEnabled(): Boolean = false

}
