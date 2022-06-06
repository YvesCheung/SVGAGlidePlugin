package com.yy.mobile.svga.glideplugin.demo

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.opensource.svgaplayer.SVGADynamicEntity
import com.opensource.svgaplayer.glideplugin.SVGATarget
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.net.URL

/**
 * @author YvesCheung
 * 2019/3/29
 */
class MainActivity : AppCompatActivity() {

    private lateinit var source: GlideSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun load() {
            source = if (rb_group.checkedRadioButtonId == R.id.rb_svga) {
                SVGASource()
            } else {
                GifSource()
            }
            loadFromRes(rb_group)
        }

        rb_group.setOnCheckedChangeListener { _, _ ->
            load()
        }
        load()
    }

    fun loadFromNetwork(v: View) {
        val (_, url) = source.getNetworkUrl()
        loadFromUrl(url)
    }

    fun loadFromAssets(v: View) {
        val (_, file) = source.getLocalFile()
        loadFromUrl(file)
    }

    fun loadFromFile(v: View) {
        val (_, url, fileName) = source.getNetworkUrl()
        val file = File(externalCacheDir, fileName.replace("/", "_"))
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

                v.post { loadFromUrl(file.absolutePath) }
            } catch (e: Exception) {
                Log.e("Yves", e.message, e)
            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    fun loadFromRes(v: View) {
        val (_, id) = source.getResId()
        tv_assets_name.text =
            "R.${this.resources.getResourceTypeName(id)}.${this.resources.getResourceEntryName(id)}"
        Glide.with(this).load(id).into(iv_img)
    }

    private fun loadFromUrl(url: String) {
        tv_assets_name.text = url
        Glide.with(this).load(url).into(iv_img)
    }

    @SuppressLint("SetTextI18n")
    fun loadSVGAFromNetworkAndAddText(v: View) {
        tv_assets_name.text = "Add SVGADynamicEntity..."
        GlideApp.with(this)
            .asSVGA()
            .load("https://github.com/yyued/SVGA-Samples/blob/master/kingset.svga?raw=true")
            .into(SVGATarget(iv_img, requestDynamicItemWithSpannableText()))
    }

    private fun requestDynamicItemWithSpannableText(): SVGADynamicEntity {
        val dynamicEntity = SVGADynamicEntity()
        val spannableStringBuilder = SpannableStringBuilder("Pony 送了一打风油精给主播")
        spannableStringBuilder.setSpan(
            ForegroundColorSpan(Color.YELLOW), 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        val textPaint = TextPaint()
        textPaint.color = Color.WHITE
        textPaint.textSize = 28f
        dynamicEntity.setDynamicText(
            StaticLayout(
                spannableStringBuilder,
                0,
                spannableStringBuilder.length,
                textPaint,
                0,
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
            ),
            "banner"
        )
        dynamicEntity.setDynamicDrawer({ canvas, frameIndex ->
            val aPaint = Paint()
            aPaint.color = Color.WHITE
            canvas.drawCircle(50f, 54f, (frameIndex % 5).toFloat(), aPaint)
            false
        }, "banner")
        return dynamicEntity
    }

    abstract class GlideSource {

        private var curIdx = 0

        @RawRes
        protected abstract fun getResId(idx: Int): Int

        protected abstract fun getFileName(idx: Int): String

        open fun getLocalFile(): GlideModel<String> =
            GlideModel(
                curIdx,
                "file:///android_asset/${getFileName(curIdx)}",
                getFileName(curIdx++)
            )

        open fun getNetworkUrl(): GlideModel<String> =
            GlideModel(
                curIdx,
                "https://github.com/YvesCheung/SVGAGlidePlugin/blob/master/" +
                        "app/src/main/assets/${getFileName(curIdx)}?raw=true",
                getFileName(curIdx++)
            )

        open fun getResId(): GlideModel<Int> =
            GlideModel(
                curIdx,
                getResId(curIdx),
                getFileName(curIdx++)
            )

        data class GlideModel<Model>(
            val index: Int,
            val model: Model,
            val fileName: String
        )
    }

    class SVGASource : GlideSource() {

        private val svgaFiles = listOf(
            "alarm",
            "jojo_audio",
            "angel",
            "EmptyState",
            "heartbeat",
            "posche",
            "rose_1.5.0",
            "rose_2.0.0"
        )

        private val svgaResources = listOf(
            R.raw.alarm,
            R.raw.jojo_audio,
            R.raw.angel,
            R.raw.emptystate,
            R.raw.heartbeat,
            R.raw.posche,
            R.raw.rose_1_5,
            R.raw.rose_2_0
        )

        @RawRes
        override fun getResId(idx: Int): Int = svgaResources[idx % svgaResources.size]

        override fun getFileName(idx: Int): String = "${svgaFiles[idx % svgaFiles.size]}.svga"
    }

    class GifSource : GlideSource() {

        private val gifFiles = listOf(
            "giphy",
            "pivot_wave",
            "rotating_earth",
            "sad_pikachu",
            "tenor"
        )

        private val gifResources = listOf(
            R.raw.giphy,
            R.raw.pivot_wave,
            R.raw.rotating_earth,
            R.raw.sad_pikachu,
            R.raw.tenor
        )

        @RawRes
        override fun getResId(idx: Int): Int = gifResources[idx % gifResources.size]

        override fun getFileName(idx: Int): String = "${gifFiles[idx % gifFiles.size]}.gif"

    }
}
