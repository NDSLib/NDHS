package com.ndsl.ndhs.mode

import com.github.bun133.nngraphics.display.Scene
import com.ndsl.ndhs.NDHS
import com.ndsl.ndhs.manager.Registry
import com.ndsl.ndhs.util.StaticNamed

abstract class Mode : StaticNamed {
    abstract fun getScene(): Scene

    /**
     * Called when the mode is changed to this mode
     */
    abstract fun onChanged()
}


class ModeManager(val ndhs: NDHS) {
    val modes = Registry<Mode>()

    init {
        modes.add(Video())
        modes.add(Streaming())
        modes.add(Picture())
    }
}