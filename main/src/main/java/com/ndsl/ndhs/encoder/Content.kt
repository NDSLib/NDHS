package com.ndsl.ndhs.encoder

import com.ndsl.ndhs.util.Named
import java.awt.image.BufferedImage
import java.nio.Buffer

/**
 * TimeLineにあるあの物体のこと
 */
abstract class Content<T> : Named() {
    abstract fun begin():Long
    abstract fun end():Long
    fun length() = end() - begin()
    abstract fun filters():MutableList<Filter<T>>
    abstract fun source():Clip<T>
}

abstract class VideoContent:Content<BufferedImage>()

abstract class AudioContent:Content<Array<Buffer>>()