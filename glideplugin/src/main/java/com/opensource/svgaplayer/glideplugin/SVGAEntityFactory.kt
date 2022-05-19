package com.opensource.svgaplayer.glideplugin

import android.content.res.AssetFileDescriptor
import android.content.res.Resources
import android.net.Uri
import com.bumptech.glide.load.data.DataRewinder
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.load.model.ResourceLoader
import com.bumptech.glide.load.model.StringLoader
import com.bumptech.glide.load.model.UrlUriLoader
import java.io.File
import java.io.InputStream

/**
 * @author YvesCheung
 * 2018/12/3
 */
internal class SVGAUrlLoaderFactory(
    private val cachePath: String,
    private val obtainRewind: (InputStream) -> DataRewinder<InputStream>
) : ModelLoaderFactory<GlideUrl, File> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, File> {
        return SVGAEntityUrlLoader(
            multiFactory.build(GlideUrl::class.java, InputStream::class.java),
            cachePath, obtainRewind)
    }

    override fun teardown() {
        //do nothing
    }
}

internal class SVGAAssetLoaderFactory(
    private val cachePath: String,
    private val obtainRewind: (InputStream) -> DataRewinder<InputStream>
) : ModelLoaderFactory<Uri, File> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Uri, File> {
        return SVGAEntityAssetLoader(
            multiFactory.build(Uri::class.java, InputStream::class.java),
            cachePath, obtainRewind)
    }

    override fun teardown() {
        //Do Nothing
    }
}

internal class SVGAStringLoaderFactory : ModelLoaderFactory<String, File> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<String, File> {
        return StringLoader(multiFactory.build(Uri::class.java, File::class.java))
    }

    override fun teardown() {
        //Do nothing
    }
}

internal class SVGAUriLoaderFactory : ModelLoaderFactory<Uri, File> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Uri, File> {
        return UrlUriLoader(multiFactory.build(GlideUrl::class.java, File::class.java))
    }

    override fun teardown() {
        //Do Nothing
    }

}

internal class SVGAResourceLoaderFactory(
    private val resource: Resources,
    private val cachePath: String,
    private val obtainRewinder: (InputStream) -> DataRewinder<InputStream>
) : ModelLoaderFactory<Int, File> {

    companion object {
        /**
         * 在RePlugin的插件中发现无法用常规方法来加载 R 的资源，
         * 因此另外使用 [SVGAEntityIntResourceLoader] 来处理这种情况。
         */
        private const val RePluginMode = true
    }

    @Suppress("ConstantConditionIf")
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Int, File> {
        return if (RePluginMode) {
            SVGAEntityIntResourceLoader(resource, cachePath, obtainRewinder)
        } else {
            ResourceLoader(resource,
                multiFactory.build(Uri::class.java, File::class.java))
        }
    }

    override fun teardown() {
        //Do Nothing
    }
}

internal class SVGAUriResourceLoaderFactory : ModelLoaderFactory<Uri, InputStream> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Uri, InputStream> {
        return SVGAEntityUriResourceLoader(
            multiFactory.build(Uri::class.java, AssetFileDescriptor::class.java))
    }

    override fun teardown() {
        //Do Nothing
    }
}