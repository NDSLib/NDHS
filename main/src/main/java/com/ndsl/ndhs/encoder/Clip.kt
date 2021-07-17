package com.ndsl.ndhs.encoder

import com.ndsl.ndhs.cache.CachedImage
import com.ndsl.ndhs.cache.CachedVideo
import com.ndsl.ndhs.filter.Filter
import java.awt.image.BufferedImage
import java.nio.Buffer

/**
 * ClipBase
 */
abstract class Clip<T> {
    abstract fun get(index: Long): T?
    abstract fun length(): Long
}

class FilteredVideoClip(val video: CachedVideo, var filters: List<Filter<BufferedImage>>) : VideoClip(video) {
    override fun get(index: Long): BufferedImage? {
        var buf: BufferedImage? = video.get(index) ?: return null
        filters.forEach {
            buf = it.filter(buf!!)
        }
        return buf
    }

    override fun length(): Long = video.length()
}

class ImageClip(val image: CachedImage) : Clip<BufferedImage>() {
    override fun get(index: Long): BufferedImage? = image.get(0)
    override fun length(): Long = 1
}

/**
 * 映像のClip
 */
open class VideoClip(val clip: CachedVideo) : Clip<BufferedImage>() {
    override fun get(index: Long): BufferedImage? = clip.get(index)
    override fun length(): Long = clip.length()
}

/**
 * 音声のClip
 */
abstract class AudioClip : Clip<Array<Buffer>>()