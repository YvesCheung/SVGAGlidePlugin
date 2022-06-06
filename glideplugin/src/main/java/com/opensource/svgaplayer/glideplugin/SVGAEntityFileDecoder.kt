package com.opensource.svgaplayer.glideplugin

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool
import com.opensource.svgaplayer.SVGAVideoEntity
import com.opensource.svgaplayer.proto.MovieEntity
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

/**
 * @author YvesCheung
 * 2018/11/28
 */
internal class SVGAEntityFileDecoder(
    private val arrayPool: ArrayPool
) : ResourceDecoder<File, SVGAVideoEntity> {

    override fun handles(source: File, options: Options): Boolean =
        source.isSVGAUnZipFile() || source.isSVGACacheFile()

    override fun decode(source: File, width: Int, height: Int, options: Options): SVGAEntityResource? {
        return if (source.isSVGAUnZipFile()) {
            decodeUnZipFile(source)
        } else {
            //is cache
            decodeCacheFile(source)
        }
    }

    private fun decodeUnZipFile(source: File): SVGAEntityResource? {
        val binaryFile = File(source, movieBinary)
        val jsonFile = File(source, movieSpec)
        if (binaryFile.isFile) {
            return parseBinaryFile(source, binaryFile)
        } else if (jsonFile.isFile) {
            return parseSpecFile(source, jsonFile)
        }
        return null
    }

    private fun parseBinaryFile(source: File, binaryFile: File): SVGAEntityResource? {
        try {
            FileInputStream(binaryFile).use {
                val entity = SVGAVideoEntity(MovieEntity.ADAPTER.decode(it), source)
                SVGAMovieAudioHelper.setupAudios(entity)
                return SVGAEntityResource(entity, source.totalSpace.toInt())
            }
        } catch (e: Exception) {
            binaryFile.delete()
            return null
        }
    }

    private fun parseSpecFile(source: File, jsonFile: File): SVGAEntityResource? {
        val buffer = arrayPool.get(ArrayPool.STANDARD_BUFFER_SIZE_BYTES, ByteArray::class.java)
        try {
            FileInputStream(jsonFile).use { fileInputStream ->
                ByteArrayOutputStream().use { byteArrayOutputStream ->
                    while (true) {
                        val size = fileInputStream.read(buffer)
                        if (size == -1) {
                            break
                        }
                        byteArrayOutputStream.write(buffer, 0, size)
                    }
                    val jsonObj = JSONObject(byteArrayOutputStream.toString())
                    val entity = SVGAVideoEntity(jsonObj, source)
                    return SVGAEntityResource(entity, source.totalSpace.toInt())
                }
            }
        } catch (e: Exception) {
            jsonFile.delete()
            return null
        } finally {
            arrayPool.put(buffer)
        }
    }

    private fun decodeCacheFile(source: File): SVGAEntityResource? {
        val buffer = arrayPool.get(ArrayPool.STANDARD_BUFFER_SIZE_BYTES, ByteArray::class.java)
        try {
            var realFilePath: String? = null
            FileInputStream(source).use { input ->
                ByteArrayOutputStream().use { byteBuffer ->
                    if (SVGACacheFileHandler.readHeadAsSVGA(input)) {
                        while (true) {
                            val len = input.read(buffer)
                            if (len <= 0) break

                            byteBuffer.write(buffer, 0, len)
                        }
                        realFilePath = String(byteBuffer.toByteArray(), Charsets.UTF_8)
                    }
                }
            }
            realFilePath?.let { path ->
                return decodeUnZipFile(File(path))
            }
            return null
        } catch (e: Throwable) {
            return null
        } finally {
            arrayPool.put(buffer)
        }
    }
}