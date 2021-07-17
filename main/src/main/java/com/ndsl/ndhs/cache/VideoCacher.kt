package com.ndsl.ndhs.cache

import com.ndsl.ndhs.NDHS
import com.ndsl.ndhs.encoder.Clip
import com.ndsl.ndhs.util.child
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO

/**
 * HDDorメモリなどにキャッシュする
 */
abstract class VideoCacher : ClipCacher<BufferedImage>() {
}

open class CachedVideo(cacher: VideoCacher, val videoUUID: UUID) : CachedClip<BufferedImage>(cacher) {
}

class DefaultVideoCacher(val workingFolder: File, val ndhs: NDHS) : VideoCacher() {
    var cacheFolder = File(workingFolder.absolutePath + "\\cache\\video")
        .also {
            if (!it.exists()) {
                if (it.mkdirs()) throw IOException("In DefaultVideoCacher,Cache Folder cannot be exist")
            }
        }

    override fun get(clip: CachedClip<BufferedImage>, index: Long): BufferedImage? {
        if (clip is CachedVideo) {
            return pull(clip.videoUUID, index)
        }
        return null
    }

    override fun cache(clip: Clip<BufferedImage>): CachedClip<BufferedImage> {
        return push(clip)
    }

    override fun unCache(clip: CachedClip<BufferedImage>): Boolean {
        if (clip is CachedVideo) {
            return remove(clip)
        }
        return false
    }

    override fun isCacheable(clip: Clip<BufferedImage>): Boolean = clip is CachedVideo || clip.length() > 1L


    // 所定の場所に書き込み
    fun push(video: Clip<BufferedImage>): CachedClip<BufferedImage> {
        val uuid = UUID.randomUUID()

        for (index in 0 until video.length()) {
            push(video.get(index), uuid, index)
        }

        return CachedVideo(this, uuid)
    }

    private fun push(video: BufferedImage?, uuid: UUID, index: Long) {
        if (video != null) {
            val filename = getFileName(uuid, index)
            val pngFile = cacheFolder.child(filename + "")

            // Write
            ImageIO.write(video, "png", pngFile)
        }
    }

    fun pull(uuid: UUID, index: Long): BufferedImage? {
        val filename = getFileName(uuid, index)
        val pngFile = cacheFolder.child(filename + "")
        return try {
            ImageIO.read(pngFile)
        } catch (e: IOException) {
            null
        }
    }

    fun remove(clip: CachedVideo): Boolean {
        return (0 until clip.length()).any {
            val filename = getFileName(clip.videoUUID, it)
            val pngFile = cacheFolder.child(filename + "")
            if (pngFile.exists()) {
                // TODO どっちにするか悩む
                pngFile.deleteOnExit()
//                pngFile.delete()


                true
            } else false
        }
    }

    /**
     * @return the cache file name with .png
     */
    fun getFileName(uuid: UUID, index: Long) = getFileName(uuid.toString(), index)

    /**
     * @return the cache file name with .png
     */
    fun getFileName(uuid: String, index: Long): String {
        return "cache-video-${uuid}-${index}.png"
    }
}