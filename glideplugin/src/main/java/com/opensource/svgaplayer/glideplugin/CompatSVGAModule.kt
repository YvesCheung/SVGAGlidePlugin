package com.opensource.svgaplayer.glideplugin

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry

/**
 * GlideModule Compatible with Android Manifest Declarations.
 *
 * @see SVGAModule
 *
 * @author YvesCheung
 * 2018/11/30.
 */
@Deprecated(message = "Replaced by [SVGAModule] for Applications that use Glide's annotations.")
class CompatSVGAModule : com.bumptech.glide.module.GlideModule {

    private val actualModule by lazy(LazyThreadSafetyMode.NONE) { SVGAModule() }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        //Do nothing
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        actualModule.registerComponents(context, glide, registry)
    }
}