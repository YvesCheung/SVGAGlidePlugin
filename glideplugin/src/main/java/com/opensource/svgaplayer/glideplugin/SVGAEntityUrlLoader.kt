package com.opensource.svgaplayer.glideplugin

import com.bumptech.glide.load.Key
import com.bumptech.glide.load.data.DataRewinder
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import java.io.InputStream
import java.security.MessageDigest

/**
 * @author YvesCheung
 * 2018/11/27
 */
internal class SVGAEntityUrlLoader(
    actual: ModelLoader<GlideUrl, InputStream>,
    cachePath: String,
    obtainRewind: (InputStream) -> DataRewinder<InputStream>
) : SVGAEntityLoader<GlideUrl>(actual, cachePath, obtainRewind) {

    override fun handles(model: GlideUrl) =
        model.toStringUrl().substringBefore('?').endsWith(".svga") &&
            super.handles(model)

    override fun toStringKey(model: GlideUrl): String = model.toStringUrl()

    /**
     * Note: why don't i use `GlideUrl` to play the role of Key?
     * because it will make a mistake whether the cache is [GlideUrl->InputStream] or
     * [GlideUrl->File]. so i use a wrapper key to cache the [GlideUrl->File].
     */
    override fun toGlideKey(model: GlideUrl): Key = WrapGlideUrl(model)

    private class WrapGlideUrl(private val actual: GlideUrl) : Key {

        private val cacheByte by lazy(LazyThreadSafetyMode.NONE) {
            "fileWrapper:${actual.cacheKey}".toByteArray(Key.CHARSET)
        }

        override fun updateDiskCacheKey(messageDigest: MessageDigest) {
            messageDigest.update(cacheByte)
        }

        override fun hashCode() = actual.hashCode()

        override fun equals(other: Any?): Boolean {
            if (other is WrapGlideUrl) {
                return actual == other.actual
            }
            return false
        }
    }
}

