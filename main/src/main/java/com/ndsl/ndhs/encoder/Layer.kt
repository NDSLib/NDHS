package com.ndsl.ndhs.encoder

import java.awt.image.BufferedImage
import java.nio.Buffer

abstract class Layer<T> {
    abstract fun getContents():MutableList<Content<T>>
}

abstract class VideoLayer:Layer<BufferedImage>()

abstract class AudioLayer:Layer<Array<Buffer>>()