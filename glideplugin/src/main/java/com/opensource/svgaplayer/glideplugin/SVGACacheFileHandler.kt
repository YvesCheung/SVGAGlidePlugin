package com.opensource.svgaplayer.glideplugin

import java.io.InputStream
import java.io.OutputStream

/**
 * @author YvesCheung
 * 2018/12/4
 */
object SVGACacheFileHandler {

    private const val SVGAMark = 0x0FABCDEF

    fun writeHead(stream: OutputStream) {
        stream.write(SVGAMark.toByteArray())
    }

    fun readHeadAsSVGA(stream: InputStream): Boolean {
        val svgaMarkExpect = ByteArray(4)
        stream.read(svgaMarkExpect)
        return svgaMarkExpect.toInt() == SVGAMark
    }

    fun isSVGAMark(byteArray: ByteArray): Boolean {
        return byteArray.toInt() == SVGAMark
    }

    private fun Int.toByteArray(): ByteArray {
        val i = this
        return ByteArray(4) {
            when (it) {
                0 -> (i shr 24).toByte()
                1 -> (i shr 16).toByte()
                2 -> (i shr 8).toByte()
                else -> i.toByte()
            }
        }
    }

    private fun ByteArray.toInt(): Int {
        if (this.size < 4) throw RuntimeException("this byteArray should be at least 4 bytes")
        var value = 0
        // 由高位到低位
        repeat(4) { i ->
            val shift = (3 - i) * 8
            value += (this[i].toInt() and 0xFF) shl shift
        }
        return value
    }
}