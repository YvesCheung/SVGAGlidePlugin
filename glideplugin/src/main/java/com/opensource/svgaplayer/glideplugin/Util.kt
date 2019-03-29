@file:Suppress("unused")

package com.opensource.svgaplayer.glideplugin

import com.bumptech.glide.RequestManager
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAVideoEntity
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * Created by 张宇 on 2018/11/26.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
const val BUCKET_SVGA = "SVGA"

fun RequestManager.asSVGA() = `as`(SVGAVideoEntity::class.java)

fun RequestManager.asSVGADrawable() = `as`(SVGADrawable::class.java)

const val movieBinary = "movie.binary"

const val movieSpec = "movie.spec"

fun File.isSVGAUnZipFile(): Boolean {

    fun hasChild(vararg fileNames: String): Boolean {
        if (this.isDirectory) {
            val childFileNames = this.list().toSet()
            return fileNames.any { childFileNames.contains(it) }
        }
        return false
    }

    return hasChild(movieBinary, movieSpec)
}

fun File.isSVGACacheFile(): Boolean {
    try {
        FileInputStream(this).use { input ->
            return SVGACacheFileHandler.readHeadAsSVGA(input)
        }
    } catch (e: IOException) {
        return false
    }
}