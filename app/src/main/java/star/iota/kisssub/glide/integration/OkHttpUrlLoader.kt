package star.iota.kisssub.glide.integration

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import okhttp3.Call
import okhttp3.OkHttpClient
import java.io.InputStream


class OkHttpUrlLoader(private val client: Call.Factory) : ModelLoader<GlideUrl, InputStream> {

    override fun handles(url: GlideUrl): Boolean {
        return true
    }

    override fun buildLoadData(model: GlideUrl, width: Int, height: Int,
                               options: Options): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(model, OkHttpStreamFetcher(client, model))
    }


    class Factory @JvmOverloads constructor(private val client: Call.Factory = getClient()) : ModelLoaderFactory<GlideUrl, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> {
            return OkHttpUrlLoader(client)
        }

        override fun teardown() {
            // Do nothing, this instance doesn't own the client.
        }

        companion object {
            @Volatile private var internalClient: Call.Factory? = null

            private fun getClient(): Call.Factory {
                if (internalClient == null) {
                    synchronized(Factory::class.java) {
                        if (internalClient == null) {
                            internalClient = OkHttpClient()
                        }
                    }
                }
                return internalClient!!
            }
        }
    }
}
