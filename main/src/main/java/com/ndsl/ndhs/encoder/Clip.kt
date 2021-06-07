package com.ndsl.ndhs.encoder

import java.awt.image.BufferedImage
import java.nio.Buffer

/**
 * ClipBase
 */
abstract class Clip<T> {
    abstract fun getAt(index: Long): T
}

/**
 * 映像のClip
 */
abstract class VideoClip: Clip<BufferedImage>()

/**
 * 音声のClip
 */
abstract class AudioClip: Clip<Array<Buffer>>()