package com.opensource.svgaplayer.glideplugin

import com.opensource.svgaplayer.SVGAVideoEntity
import com.opensource.svgaplayer.proto.MovieEntity
import java.lang.reflect.Method
import java.util.concurrent.CountDownLatch

/**
 * Hook [SVGAVideoEntity.setupAudios] to prepare audio
 *
 * @author YvesCheung
 * 2022/6/6
 */
object SVGAMovieAudioHelper {

    fun setupAudios(entity: SVGAVideoEntity) {
        val movie = entity.movieItem
        if (movie != null) {
            val method = setupAudiosMethod
            if (method != null) {
                val waitSync = CountDownLatch(1)
                val callback: () -> Unit = { //callback in mainThread
                    waitSync.countDown()
                }
                try {
                    method.invoke(entity, movie, callback)
                    waitSync.await() //await in GlideThread
                } catch (ignore: Throwable) {
                    //Ignore
                }
            }
        }
    }

    private val setupAudiosMethod: Method? by lazy {
        val method = try {
            //>= SVGAPlayer 2.6.x
            SVGAVideoEntity::class.java.getDeclaredMethod(
                "setupAudios",
                MovieEntity::class.java,
                kotlin.jvm.functions.Function0::class.java
            )
        } catch (e: Throwable) {
            try {
                //<= SVGAPlayer 2.5.x
                SVGAVideoEntity::class.java.getDeclaredMethod(
                    "resetAudios",
                    MovieEntity::class.java,
                    kotlin.jvm.functions.Function0::class.java
                )
            } catch (e: Throwable) {
                null
            }
        }
        if (method != null) {
            method.isAccessible = true
        }
        method
    }
}