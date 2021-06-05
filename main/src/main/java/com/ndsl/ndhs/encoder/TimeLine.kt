package com.ndsl.ndhs.encoder

import com.ndsl.ndhs.util.Named

/**
 * TimeLine全体(何ならこれがプロジェクト全体まである)
 */
abstract class TimeLine : Named() {
    abstract fun layers(): MutableList<Layer>
    abstract fun getAt(index: Long):MutableMap<Layer,Content>
}