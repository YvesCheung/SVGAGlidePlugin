package com.opensource.svgaplayer.glideplugin

import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.drawable.DrawableResource
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAVideoEntity

/**
 * @author YvesCheung
 * 2018/11/26
 */
class SVGADrawableResource(drawable: SVGADrawable, private val entityRes: Resource<SVGAVideoEntity>) :
    DrawableResource<SVGADrawable>(drawable) {

    override fun getResourceClass() = SVGADrawable::class.java

    override fun getSize(): Int = entityRes.size

    override fun recycle() {
        entityRes.recycle()
    }
}