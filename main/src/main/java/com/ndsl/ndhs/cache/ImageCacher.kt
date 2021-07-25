package com.ndsl.ndhs.cache

import com.ndsl.ndhs.NDHS
import com.ndsl.ndhs.encoder.Clip
import com.ndsl.ndhs.manager.Registry
import com.ndsl.ndhs.util.child
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO

/**
 * HDDorメモリなどにキャッシュする
 */
abstract class ImageCacher : ClipCacher<BufferedImage>() {
}

open class CachedImage(cacher: ImageCacher, val imageUUID: UUID) : CachedClip<BufferedImage>(cacher) {
}

class DefaultImageCacher(val workingFolder: File, val ndhs: NDHS) : ImageCacher() {
    var cacheFolder = File(workingFolder.absolutePath + "\\cache\\image")
        .also {
            if (!it.exists()) {
                if (!it.mkdirs()) throw IOException("In DefaultVideoCacher,Cache Folder cannot be exist")
            }
        }

    override fun get(clip: CachedClip<BufferedImage>, index: Long): BufferedImage? {
        if (clip is CachedImage) {
            return pull(clip)
        }
        return null
    }

    override fun cache(clip: Clip<BufferedImage>): CachedClip<BufferedImage> {
        val uuid = UUID.randomUUID()
        push(clip.get(0), uuid)
        return CachedImage(this, uuid)
    }

    override fun unCache(clip: CachedClip<BufferedImage>): Boolean {
        if (clip is CachedImage) {
            return remove(clip.imageUUID)
        }
        return false
    }

    override fun isCacheable(clip: Clip<*>): Boolean = clip is CachedImage || clip.length() == 1L

    override fun getLength(cachedClip: CachedClip<BufferedImage>): Long {
        return if (cachedClip is CachedImage) {
            if (getFile(cachedClip.imageUUID).exists()) {
                1
            } else {
                -1
            }
        } else {
            -1
        }
    }

    fun pull(image: CachedImage): BufferedImage? {
        val pngFile = getFile(image.imageUUID)
        return try {
            ImageIO.read(pngFile)
        } catch (e: IOException) {
            null
        }
    }


    fun push(image: BufferedImage?, uuid: UUID) {
        if (image != null) {
            val pngFile = getFile(uuid)

            // Write
            ImageIO.write(image, "png", pngFile)
        }
    }

    fun remove(uuid: UUID): Boolean {
        val pngFile = getFile(uuid)
        if (pngFile.exists()) {
            // TODO どっちにするか悩む
            pngFile.deleteOnExit()
//                pngFile.delete()
            return true
        }
        return false
    }


    /**
     * @return the cache file name with .png
     */
    fun getFileName(uuid: UUID) = getFileName(uuid.toString())

    /**
     * @return the cache file name with .png
     */
    fun getFileName(uuid: String): String {
        return "cache-image-${uuid}.png"
    }

    fun getFile(uuid: UUID): File {
        val filename = getFileName(uuid)
        return cacheFolder.child(filename + "")
    }
}