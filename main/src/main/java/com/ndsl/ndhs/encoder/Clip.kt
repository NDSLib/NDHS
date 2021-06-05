package com.ndsl.ndhs.encoder

import java.awt.image.BufferedImage

/**
 * 映像のClip
 */
abstract class Clip {
    abstract fun getFrame(index: Long): BufferedImage
}