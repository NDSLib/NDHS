package com.ndsl.ndhs

import com.ndsl.ndhs.encoder.Clip

/**
 * キャッシュしてくれるやつ
 */
abstract class ClipCacheManager<T> {
    abstract fun get(clip:Clip<T>,index:Long):T
    abstract fun cache(clip:Clip<T>)
    abstract fun unCache(clip:Clip<T>)
}