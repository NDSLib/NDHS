package com.ndsl.ndhs.encoder

import com.ndsl.ndhs.util.Named

/**
 * @param T BufferedImage or Array<Buffer>
 */
abstract class Filter<T> : Named() {
    abstract fun onFilter(content:Content<T>):Clip<T>
}