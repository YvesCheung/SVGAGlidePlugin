package com.opensource.svgaplayer.glideplugin

import android.net.Uri
import com.bumptech.glide.load.data.DataRewinder
import com.bumptech.glide.load.model.ModelLoader
import java.io.InputStream

/**
 * @author YvesCheung
 * 2018/12/3
 */
internal class SVGAEntityAssetLoader(
    actual: ModelLoader<Uri, InputStream>,
    cachePath: String,
    obtainRewind: (InputStream) -> DataRewinder<InputStream>
) : SVGAEntityLoader<Uri>(actual, cachePath, obtainRewind) {

    override fun toStringKey(model: Uri): String = model.toString()
}