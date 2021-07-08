package com.ndsl.ndhs.cache

import com.ndsl.ndhs.NDHS
import com.ndsl.ndhs.encoder.Clip
import com.ndsl.ndhs.manager.Registry

/**
 * キャッシュしてくれるやつ
 */
abstract class ClipCache<T> {
    abstract fun get(clip: Clip<T>, index: Long): T
    abstract fun cache(clip: Clip<T>)
    abstract fun unCache(clip: Clip<T>)
}

class CachedClip<T>(val manager: ClipCache<T>, val clip: Clip<T>) {
    fun get(index: Long): T = manager.get(clip, index)
    fun cache() = manager.cache(clip)
    fun unCache() = manager.unCache(clip)
}

class ClipCacheManager(val ndhs:NDHS){
    val cache = Registry<ClipCache<*>>()
}