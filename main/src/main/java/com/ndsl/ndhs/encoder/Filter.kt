package com.ndsl.ndhs.encoder

import com.ndsl.ndhs.util.Named

abstract class Filter : Named() {
    abstract fun onFilter(content:Content):Clip
}