package com.opensource.svgaplayer.glideplugin

import com.bumptech.glide.request.target.ImageViewTarget
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGADynamicEntity
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAVideoEntity

/**
 * @author YvesCheung
 * 2018/11/29
 */
@Suppress("MemberVisibilityCanBePrivate")
open class SVGATarget(
    protected val imageView: SVGAImageView,
    protected val dynamicEntity: SVGADynamicEntity
) : ImageViewTarget<SVGAVideoEntity>(imageView) {

    override fun setResource(resource: SVGAVideoEntity?) {
        resource ?: return

        val drawable = SVGADrawable(resource, dynamicEntity)
        imageView.setImageDrawable(drawable)
        imageView.startAnimation()
    }
}