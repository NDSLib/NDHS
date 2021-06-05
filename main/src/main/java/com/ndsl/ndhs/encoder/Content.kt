package com.ndsl.ndhs.encoder

import com.ndsl.ndhs.util.Named

/**
 * TimeLineにあるあの物体のこと
 */
abstract class Content : Named() {
    abstract fun begin():Long
    abstract fun end():Long
    fun length() = end() - begin()
    abstract fun filters():MutableList<Filter>
    abstract fun source():Clip
}