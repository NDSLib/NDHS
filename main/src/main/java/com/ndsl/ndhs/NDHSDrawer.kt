package com.ndsl.ndhs

import com.github.bun133.nngraphics.display.Drawable
import com.github.bun133.nngraphics.display.Rect
import com.ndsl.ndhs.ui.UIComponent
import kotlin.concurrent.thread

class NDHSDrawer(val ndhs: NDHS) {
    val display = ndhs.newGUIWindow(Rect(100, 100, 900, 550), null, true, isUnDecorated = false)
    var drawThread: Thread? = null

    fun invokeDrawThread() {
        drawThread = thread {
            while (true) {
                display.jFrame.draw.update()
            }
        }

        initDrawer()
    }

    fun stopDrawThread() {
        drawThread?.interrupt()
    }

    fun addUI(ui: UIComponent) {
        display.UIManager.register(ui)
    }

    /**
     * Draw Init
     * (たぶん完成に一か月)
     */
    private fun initDrawer(){
        // HangBar
    }
}