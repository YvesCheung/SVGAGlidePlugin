package com.yy.mobile.svga.glideplugin.demo

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.opensource.svgaplayer.SVGADynamicEntity
import com.opensource.svgaplayer.glideplugin.SVGATarget
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val svgaFiles = listOf(
        "alarm",
        "angel",
        "EmptyState",
        "heartbeat",
        "posche",
        "rose_1.5.0",
        "rose_2.0.0")

    private val svgaResources = listOf(
        R.raw.alarm,
        R.raw.angel,
        R.raw.emptystate,
        R.raw.heartbeat,
        R.raw.posche,
        R.raw.rose_1_5,
        R.raw.rose_2_0
    )

    private var curIdx = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun loadSVGAFromNetwork(v: View) {
        val url = "https://github.com/YvesCheung/SVGAGlidePlugin/blob/master/" +
            "app/src/main/assets/${svgaFiles[curIdx]}.svga?raw=true"
        loadSVGAFromUrl(url)
    }

    fun loadSVGAFromAssets(v: View) {
        val fileUrl = "file:///android_asset/${svgaFiles[curIdx]}.svga"
        loadSVGAFromUrl(fileUrl)
    }

    fun loadSVGAFromFile(v: View) {
        val url = "https://github.com/YvesCheung/SVGAGlidePlugin/blob/master/" +
            "app/src/main/assets/${svgaFiles[curIdx]}.svga?raw=true"
        val file = File(externalCacheDir, svgaFiles[curIdx].replace("/", "_"))
        val buffer = ByteArray(1 * 1024 * 1024)
        Thread {
            try {
                with(URL(url).openConnection()) {
                    connect()
                    getInputStream().use { input ->
                        FileOutputStream(file).use { output ->
                            while (true) {
                                val len = input.read(buffer)
                                if (len <= 0) break

                                output.write(buffer, 0, len)
                            }
                        }
                    }
                }

                v.post { loadSVGAFromUrl(file.absolutePath) }
            } catch (e: Exception) {
                Log.e("Yves", e.message, e)
            }
        }.start()
    }

    fun loadSVGAFromRes(v: View) {
        @RawRes val id = svgaResources[curIdx]
        tv_assets_name.text = this.resources.getResourceEntryName(id)
        Glide.with(this).load(id).into(iv_img)
        curIdx = ++curIdx % svgaResources.size
    }

    private fun loadSVGAFromUrl(url: String) {
        curIdx = ++curIdx % svgaFiles.size
        tv_assets_name.text = url
        Glide.with(this).load(url).into(iv_img)
    }

    fun loadSVGAFromNetworkAndAddText(v: View) {
        GlideApp.with(this)
            .asSVGA()
            .load("https://github.com/yyued/SVGA-Samples/blob/master/kingset.svga?raw=true")
            .into(SVGATarget(iv_img, requestDynamicItemWithSpannableText()))
    }

    private fun requestDynamicItemWithSpannableText(): SVGADynamicEntity {
        val dynamicEntity = SVGADynamicEntity()
        val spannableStringBuilder = SpannableStringBuilder("Pony 送了一打风油精给主播")
        spannableStringBuilder.setSpan(ForegroundColorSpan(Color.YELLOW), 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        val textPaint = TextPaint()
        textPaint.color = Color.WHITE
        textPaint.textSize = 28f
        dynamicEntity.setDynamicText(StaticLayout(
            spannableStringBuilder,
            0,
            spannableStringBuilder.length,
            textPaint,
            0,
            Layout.Alignment.ALIGN_CENTER,
            1.0f,
            0.0f,
            false
        ), "banner")
        dynamicEntity.setDynamicDrawer({ canvas, frameIndex ->
            val aPaint = Paint()
            aPaint.color = Color.WHITE
            canvas.drawCircle(50f, 54f, (frameIndex % 5).toFloat(), aPaint)
            false
        }, "banner")
        return dynamicEntity
    }
}
