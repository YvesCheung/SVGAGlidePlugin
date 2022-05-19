package com.opensource.svgaplayer.glideplugin

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGADynamicEntity
import com.opensource.svgaplayer.SVGAVideoEntity

/**
 * @author YvesCheung
 * 2018/11/28
 */
class SVGADrawableTranscoder : ResourceTranscoder<SVGAVideoEntity, SVGADrawable> {

    override fun transcode(toTranscode: Resource<SVGAVideoEntity>, options: Options): SVGADrawableResource {
        return SVGADrawableResource(
            SVGADrawable(toTranscode.get(), SVGADynamicEntity()), toTranscode)
    }
}