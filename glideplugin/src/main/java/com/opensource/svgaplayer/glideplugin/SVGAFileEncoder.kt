package com.opensource.svgaplayer.glideplugin

import com.bumptech.glide.load.Encoder
import com.bumptech.glide.load.Options
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * @author YvesCheung
 * 2018/12/4
 */
class SVGAFileEncoder : Encoder<File> {
    override fun encode(data: File, file: File, options: Options): Boolean {
        var success = false
        if (data.isSVGAUnZipFile()) {
            val path = data.absolutePath
            try {
                FileOutputStream(file).use { output ->
                    SVGACacheFileHandler.writeHead(output)
                    output.write(path.toByteArray(Charsets.UTF_8))
                }
                success = true
            } catch (e: IOException) {
                //Ignore
            }
        } else if (data.isSVGACacheFile()) {
            try {
                FileInputStream(data).channel.use { sourceChannel ->
                    FileOutputStream(file).channel.use { destChannel ->
                        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size())
                    }
                }
                success = true
            } catch (e: Exception) {
                //Ignore
            }
        }
        return success
    }
}