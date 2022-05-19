package com.opensource.svgaplayer.glideplugin

import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideType
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAVideoEntity


/**
 * @author YvesCheung
 * 2018/11/30
 */
@GlideExtension
object SVGATypeExtension {

    @JvmStatic
    @GlideType(SVGADrawable::class)
    fun asSVGADrawable(requestBuilder: RequestBuilder<SVGADrawable>): RequestBuilder<SVGADrawable> {
        return requestBuilder
    }

    @JvmStatic
    @GlideType(SVGAVideoEntity::class)
    fun asSVGA(requestBuilder: RequestBuilder<SVGAVideoEntity>): RequestBuilder<SVGAVideoEntity> {
        return requestBuilder
    }
}