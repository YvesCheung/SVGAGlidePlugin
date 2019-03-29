package com.opensource.svgaplayer.glideplugin

import android.net.Uri
import com.bumptech.glide.load.data.DataRewinder
import com.bumptech.glide.load.model.ModelLoader
import java.io.InputStream

/**
 * Created by 张宇 on 2018/12/3.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
internal class SVGAEntityAssetLoader(
    actual: ModelLoader<Uri, InputStream>,
    cachePath: String,
    obtainRewind: (InputStream) -> DataRewinder<InputStream>
) : SVGAEntityLoader<Uri>(actual, cachePath, obtainRewind) {

    override fun toStringKey(model: Uri): String = model.toString()
}