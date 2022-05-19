package com.opensource.svgaplayer.glideplugin

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.data.DataRewinder
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import java.io.InputStream

/**
 * @author YvesCheung
 * 2019/3/29
 */
class SVGAEntityIntResourceLoader(
    private val resources: Resources,
    cachePath: String,
    obtainRewind: (InputStream) -> DataRewinder<InputStream>
) : SVGAEntityLoader<Int>(SimpleResourceLoader(resources), cachePath, obtainRewind) {

    private fun getResourceUri(resources: Resources, model: Int): Uri? {
        return try {
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + resources.getResourcePackageName(model) + '/'.toString()
                + resources.getResourceTypeName(model) + '/'.toString()
                + resources.getResourceEntryName(model))
        } catch (e: Resources.NotFoundException) {
            null
        }
    }

    override fun toStringKey(model: Int): String =
        getResourceUri(resources, model)?.toString() ?: "UnknownKey$model"
}

private class SimpleResourceLoader(
    private val resources: Resources
) : ModelLoader<Int, InputStream> {

    private fun getResourceUri(resources: Resources, model: Int): Uri? {
        return try {
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + resources.getResourcePackageName(model) + '/'.toString()
                + resources.getResourceTypeName(model) + '/'.toString()
                + resources.getResourceEntryName(model))
        } catch (e: Resources.NotFoundException) {
            null
        }
    }

    override fun handles(model: Int): Boolean {
        return try {
            resources.getResourceTypeName(model).contains("raw")
        } catch (e: Exception) {
            false
        }
    }

    override fun buildLoadData(
        model: Int, width: Int, height: Int, options: Options
    ): ModelLoader.LoadData<InputStream>? {
        val uri = getResourceUri(resources, model)
        return if (uri == null) {
            null
        } else {
            ModelLoader.LoadData(ObjectKey(uri), SimpleFetcher(resources, model))
        }
    }

    private class SimpleFetcher(
        private val resources: Resources,
        private val model: Int
    ) : DataFetcher<InputStream> {

        override fun getDataClass(): Class<InputStream> = InputStream::class.java

        override fun cleanup() {
            //Do nothing.
        }

        override fun getDataSource(): DataSource = DataSource.LOCAL

        override fun cancel() {
            //Do nothing.
        }

        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
            try {
                callback.onDataReady(resources.openRawResource(model))
            } catch (e: Exception) {
                callback.onLoadFailed(e)
            }
        }
    }
}