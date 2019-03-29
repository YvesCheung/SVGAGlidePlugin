package com.opensource.svgaplayer.glideplugin

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool
import com.opensource.svgaplayer.SVGAVideoEntity
import com.opensource.svgaplayer.proto.MovieEntity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.zip.InflaterInputStream

/**
 * Created by 张宇 on 2018/11/26.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 *
 */
internal class SVGAEntityStreamDecoder(
    private val cachePath: String,
    private val arrayPool: ArrayPool
) : AbsSVGAEntityDecoder(), ResourceDecoder<InputStream, SVGAVideoEntity> {

    override fun handles(source: InputStream, options: Options): Boolean {
        val bytes = readHeadAsBytes(source)
        return if (bytes == null) {
            false
        } else {
            !bytes.isZipFormat && !SVGACacheFileHandler.isSVGAMark(bytes)
        }
    }


    override fun decode(source: InputStream, width: Int, height: Int, options: Options): SVGAEntityResource? {
        inflate(source)?.let { bytesOrigin ->
            val entity = SVGAVideoEntity(MovieEntity.ADAPTER.decode(bytesOrigin), File(cachePath))
            return SVGAEntityResource(entity, bytesOrigin.size)
        }
        return null
    }

    private fun inflate(source: InputStream): ByteArray? = attempt {
        val buffer = arrayPool.get(ArrayPool.STANDARD_BUFFER_SIZE_BYTES, ByteArray::class.java)
        try {
            InflaterInputStream(source).use { input ->
                ByteArrayOutputStream().use { output ->
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
        }
    }
}