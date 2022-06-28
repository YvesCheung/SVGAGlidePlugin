package com.opensource.svgaplayer.glideplugin

import androidx.annotation.MainThread
import com.bumptech.glide.Priority
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.data.DataRewinder
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicBoolean
import java.util.zip.ZipInputStream

/**
 * @author YvesCheung
 * 2018/12/3
 */
abstract class SVGAEntityLoader<MODEL : Any>(
    private val actual: ModelLoader<MODEL, InputStream>,
    private val cachePath: String,
    private val obtainRewind: (InputStream) -> DataRewinder<InputStream>
) : ModelLoader<MODEL, File> {

    override fun buildLoadData(model: MODEL, width: Int, height: Int, options: Options): ModelLoader.LoadData<File>? {
        val actualFetcher = actual.buildLoadData(model, width, height, options)?.fetcher
            ?: return null
        return ModelLoader.LoadData(
            toGlideKey(model),
            SVGAEntityFetcher(
                toStringKey(model),
                actualFetcher,
                cachePath,
                obtainRewind))
    }

    override fun handles(model: MODEL): Boolean = actual.handles(model)

    protected abstract fun toStringKey(model: MODEL): String

    protected open fun toGlideKey(model: MODEL): Key =
        if (model is Key) model else ObjectKey(model)

    private class SVGAEntityFetcher(
        private val modelKey: String,
        private val fetcher: DataFetcher<InputStream>,
        private val cachePath: String,
        private val obtainRewind: (InputStream) -> DataRewinder<InputStream>
    ) : AbsSVGAEntityDecoder(), DataFetcher<File> {

        private val isCanceled = AtomicBoolean()

        override fun getDataClass() = File::class.java

        override fun cleanup() {
            fetcher.cleanup()
        }

        override fun getDataSource() = fetcher.dataSource

        @MainThread
        override fun cancel() {
            isCanceled.set(true)
            fetcher.cancel()
        }

        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in File>) {
            fetcher.loadData(priority, object : DataFetcher.DataCallback<InputStream> {

                override fun onLoadFailed(e: Exception) {
                    callback.onLoadFailed(e)
                }

                override fun onDataReady(data: InputStream?) {
                    if (data == null) {
                        callback.onLoadFailed(NullPointerException("Data is null."))
                    } else {
                        val result = try {
                            decode(data)
                        } catch (e: Exception) {
                            callback.onLoadFailed(e)
                            return
                        }
                        if (result == null || !result.isDirectory) {
                            callback.onLoadFailed(NullPointerException("The result of SVGAEntityFetcher is null."))
                        } else {
                            callback.onDataReady(result)
                        }
                    }
                }
            })
        }

        private fun decode(source: InputStream): File? {
            if (isCanceled.get()) return null
            if (cacheDir.isDirectory && !cacheDir.list().isNullOrEmpty()) return cacheDir

            val rewind = obtainRewind(source)
            try {
                readHeadAsBytes(rewind.rewindAndGet())?.let { sourceHead ->
                    if (sourceHead.isZipFormat && !isCanceled.get()) {
                        try {
                            cacheDir.makeSureExist()
                            unzip(rewind.rewindAndGet(), cacheDir)
                        } catch (e: Exception) {
                            cacheDir.deleteRecursively()
                            e.printStackTrace()
                        }
                        return cacheDir
                    }
                }
            } finally {
                rewind.cleanup()
            }
            return null
        }

        private fun File.makeSureExist() {
            val dir = this
            if (dir.exists()) {
                if (!dir.isDirectory) {
                    dir.deleteRecursively()
                    dir.mkdirs()
                }
            } else {
                dir.mkdirs()
            }
        }

        private fun unzip(inputStream: InputStream, dir: File) {
            ZipInputStream(inputStream).use { zipInputStream ->
                while (true) {
                    val zipItem = zipInputStream.nextEntry ?: break
                    if (zipItem.name.contains("../")) {
                        // 解压路径防止穿透
                        continue
                    }
                    if (zipItem.name.contains("/")) {
                        continue
                    }
                    val file = File(dir, zipItem.name)
                    FileOutputStream(file).use { fileOutputStream ->
                        val buff = ByteArray(2048)
                        while (true) {
                            val readBytes = zipInputStream.read(buff)
                            if (readBytes <= 0) {
                                break
                            }
                            fileOutputStream.write(buff, 0, readBytes)
                        }
                    }
                    zipInputStream.closeEntry()
                }
            }
        }

        private fun cacheKey(str: String): String {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(str.toByteArray(charset("UTF-8")))
            val digest = messageDigest.digest()
            var sb = ""
            for (b in digest) {
                sb += String.format("%02x", b)
            }
            return sb
        }

        private val cacheDir: File by lazy(LazyThreadSafetyMode.NONE) {
            File(cachePath, cacheKey(modelKey))
        }
    }
}