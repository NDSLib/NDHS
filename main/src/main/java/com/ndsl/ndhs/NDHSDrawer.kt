package com.ndsl.ndhs

import com.github.bun133.nngraphics.display.Rect
import kotlin.concurrent.thread

class NDHSDrawer(val ndhs: NDHS) {
    val display = ndhs.newGUIWindow(Rect(100, 100, 900, 550), null)
    var drawThread: Thread? = null

    fun invokeDrawThread() {
        drawThread = thread {
            while (true) {
                display.draw.update()
            }
        }
    }
}