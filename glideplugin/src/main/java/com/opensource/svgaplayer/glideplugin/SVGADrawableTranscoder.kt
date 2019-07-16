package com.opensource.svgaplayer.glideplugin

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGADynamicEntity
import com.opensource.svgaplayer.SVGAVideoEntity

/**
 * Created by 张宇 on 2018/11/28.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class SVGADrawableTranscoder(private val wrapToAnimator: Boolean) : ResourceTranscoder<SVGAVideoEntity, Drawable> {

    override fun transcode(toTranscode: Resource<SVGAVideoEntity>, options: Options): Resource<Drawable> {
        val drawable = SVGADrawable(toTranscode.get(), SVGADynamicEntity())
        return if (wrapToAnimator) {
            SVGADrawableResource(SVGADrawableWrapper(drawable), toTranscode)
        } else {
            SVGADrawableResource(drawable, toTranscode)
        }
    }
}