package com.opensource.svgaplayer.glideplugin

import android.content.ContentResolver
import android.content.res.AssetFileDescriptor
import android.net.Uri
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import java.io.InputStream
import java.lang.Exception
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author YvesCheung
 * 2018/11/26
 */
class SVGAEntityUriResourceLoader(
    private val actual: ModelLoader<Uri, AssetFileDescriptor>
) : ModelLoader<Uri, InputStream> {

    private val schema = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"

    override fun handles(model: Uri): Boolean =
        schema == model.scheme && actual.handles(model)

    override fun buildLoadData(
        model: Uri,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        val loadData =
            actual.buildLoadData(model, width, height, options)
        val dataFetcher = loadData?.fetcher
        if (loadData != null && dataFetcher != null) {
            return ModelLoader.LoadData(loadData.sourceKey, Fetcher(dataFetcher))
        }
        return null
    }

    private class Fetcher(
        private val actual: DataFetcher<AssetFileDescriptor>
    ) : DataFetcher<InputStream> {

        private val cancel = AtomicBoolean(false)

        override fun getDataClass(): Class<InputStream> = InputStream::class.java

        override fun cleanup() {
            actual.cleanup()
        }

        override fun getDataSource(): DataSource = actual.dataSource

        override fun cancel() {
            cancel.set(true)
            actual.cancel()
        }

        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
            if (!cancel.get()) {
                actual.loadData(priority, object : DataFetcher.DataCallback<AssetFileDescriptor> {
                    override fun onLoadFailed(e: Exception) {
                        callback.onLoadFailed(e)
                    }

                    override fun onDataReady(data: AssetFileDescriptor?) {
                        if (data == null) {
                            callback.onLoadFailed(NullPointerException("AssetFileDescriptor is null."))
                            return
                        }
                        try {
                            callback.onDataReady(data.createInputStream())
                        } catch (e: Exception) {
                            callback.onLoadFailed(e)
                        }
                    }
                })
            }
        }
    }
}