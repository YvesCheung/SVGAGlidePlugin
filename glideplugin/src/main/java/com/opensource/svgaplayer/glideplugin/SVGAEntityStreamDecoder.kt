package com.opensource.svgaplayer.glideplugin

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool
import com.opensource.svgaplayer.SVGAVideoEntity
import com.opensource.svgaplayer.proto.MovieEntity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

/**
 * @author YvesCheung
 * 2018/11/26
 */
internal class SVGAEntityStreamDecoder(
    private val cachePath: String,
    private val arrayPool: ArrayPool
) : AbsSVGAEntityDecoder(), ResourceDecoder<InputStream, SVGAVideoEntity> {

    override fun handles(source: InputStream, options: Options): Boolean {
        val bytes = readHeadAsBytes(source)
        return bytes != null && bytes.isZLibFormat
    }

    override fun decode(source: InputStream, width: Int, height: Int, options: Options): SVGAEntityResource? {
        inflate(source)?.let { bytesOrigin ->
            val entity = SVGAVideoEntity(MovieEntity.ADAPTER.decode(bytesOrigin), File(cachePath))
            SVGAMovieAudioHelper.setupAudios(entity)
            return SVGAEntityResource(entity, bytesOrigin.size)
        }
        return null
    }

    private fun inflate(source: InputStream): ByteArray? = attempt {
        val buffer = arrayPool.get(ArrayPool.STANDARD_BUFFER_SIZE_BYTES, ByteArray::class.java)
        val inflater = Inflater()
        try {
            InflaterInputStream(source, inflater).let { input ->
                ByteArrayOutputStream().let { output ->
                    while (true) {
                        val cnt = input.read(buffer)
                        if (cnt <= 0) break
                        output.write(buffer, 0, cnt)
                    }
                    output.toByteArray()
                }
            }
        } finally {
            arrayPool.put(buffer)
            inflater.end()
        }
    }
}