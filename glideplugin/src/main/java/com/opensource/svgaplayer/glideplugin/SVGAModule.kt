package com.opensource.svgaplayer.glideplugin

import android.content.Context
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideContext
import com.bumptech.glide.Registry
import com.bumptech.glide.Registry.BUCKET_BITMAP
import com.bumptech.glide.Registry.BUCKET_BITMAP_DRAWABLE
import com.bumptech.glide.Registry.BUCKET_GIF
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.LibraryGlideModule
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAVideoEntity
import java.io.File
import java.io.InputStream

/**
 * Created by 张宇 on 2018/11/26.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
@GlideModule
class SVGAModule : LibraryGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.setResourceDecoderBucketPriorityList(
            listOf(BUCKET_SVGA, BUCKET_BITMAP, BUCKET_BITMAP_DRAWABLE, BUCKET_GIF)
        )
        hookTheImageViewFactory(glide)
        val resources = context.resources
        val cachePath = context.cacheDir.absolutePath
        val streamDecoder = SVGAEntityStreamDecoder(cachePath, glide.arrayPool)
        val resourceFactory = SVGAResourceLoaderFactory(resources, cachePath, registry::getRewinder)
        registry
            .register(SVGAVideoEntity::class.java, SVGADrawable::class.java, SVGADrawableTranscoder())
            .append(BUCKET_SVGA, InputStream::class.java, SVGAVideoEntity::class.java,
                streamDecoder)
            .append(BUCKET_SVGA, File::class.java, SVGAVideoEntity::class.java,
                SVGAEntityFileDecoder(glide.arrayPool))
            // int/Uri for R.raw.resourceId
            .append(Int::class.java, File::class.java, resourceFactory)
            .append(Int::class.javaObjectType, File::class.java, resourceFactory)
            .append(Uri::class.java, InputStream::class.java, SVGAUriResourceLoaderFactory())
            // Uri for file://android_asset
            .append(Uri::class.java, File::class.java, SVGAAssetLoaderFactory(cachePath, registry::getRewinder))
            // String/Uri/GlideUrl for http:/https:
            .append(String::class.java, File::class.java, SVGAStringLoaderFactory())
            .append(Uri::class.java, File::class.java, SVGAUriLoaderFactory())
            .append(GlideUrl::class.java, File::class.java, SVGAUrlLoaderFactory(cachePath, registry::getRewinder))
            // encode to disk
            .append(File::class.java, SVGAFileEncoder())
    }

    private fun hookTheImageViewFactory(glide: Glide) {
        try {
            val imageFactory = GlideContext::class.java.getDeclaredField("imageViewTargetFactory")
                ?: return
            val glideContext = Glide::class.java.getDeclaredField("glideContext")
                ?: return
            glideContext.isAccessible = true
            imageFactory.isAccessible = true

            imageFactory.set(glideContext.get(glide), SVGAImageViewTargetFactory())

        } catch (e: Exception) {
            Log.e("SVGAPlayer", e.message, e)
        }
    }
}

