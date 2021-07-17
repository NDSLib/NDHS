package com.ndsl.ndhs.cache

import com.ndsl.ndhs.NDHS
import com.ndsl.ndhs.manager.Registry
import java.awt.Image
import java.awt.image.BufferedImage

/**
 * HDDorメモリなどにキャッシュする
 */
abstract class ImageCacher : ClipCacher<BufferedImage>() {
    abstract fun get(img: CachedImage): BufferedImage
    abstract fun cache(img: Image): CachedImage
    abstract fun unCache(img: CachedImage)
}

open class CachedImage(cacher: ImageCacher) : CachedClip<BufferedImage>(cacher) {
}

class DefaultImageCacher(){

}