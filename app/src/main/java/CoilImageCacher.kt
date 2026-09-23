import android.content.Context
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoilImageCacher @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun queueImageCacheRequest(url: String) {
        val request = ImageRequest.Builder(context)
            .data(url)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
        context.imageLoader.enqueue(request)
    }

    fun queueImageCacheRequests(urls: List<String>) {
        urls.forEach { url ->
            queueImageCacheRequest(url)
        }
    }
}