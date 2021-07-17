package com.ndsl.ndhs.cache

import com.ndsl.ndhs.NDHS
import com.ndsl.ndhs.encoder.Clip
import com.ndsl.ndhs.manager.Registry

/**
 * キャッシュしてくれるやつ
 */
abstract class ClipCacher<T> {
    abstract fun get(clip: CachedClip<T>, index: Long): T?
    abstract fun cache(clip: Clip<T>): CachedClip<T>
    // UnCacheできたらtrue
    abstract fun unCache(clip: CachedClip<T>): Boolean
    // Clipがキャッシュ出来そうか出来なさそうか
    abstract fun isCacheable(clip:Clip<T>):Boolean
}

/**
 * キャッシュされたClip
 */
open class CachedClip<T>(val cacher: ClipCacher<T>) : Clip<T>() {
    override fun get(index: Long): T? = cacher.get(this, index)
    fun unCache() = cacher.unCache(this)
    override fun length(): Long {
        TODO("Not yet implemented")
    }
}

class ClipCacheManager(val ndhs: NDHS) {
    val cacher = Registry<ClipCacher<*>>()

    init {
        // Defaults
        cacher.add(DefaultVideoCacher(ndhs.getWorkingFolder(),ndhs))
    }
}