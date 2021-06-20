package com.ndsl.ndhs.util

import com.github.bun133.nngraphics.display.Rect
import java.awt.Graphics
import java.awt.geom.Rectangle2D

fun Graphics.drawRect(r: Rect) {
    drawRect(r.left_up.x, r.left_up.y, r.width(), r.height())
}

fun Graphics.stringBound(str: String): Rect {
    val b = Rect(this.fontMetrics.getStringBounds(str, this))
    return Rect(b.left_up.x, b.left_up.y, b.right_down.x + fontMetrics.maxAdvance, b.right_down.y)
}

fun Graphics.fillRect(r: Rect) {
    fillRect(r.left_up.x, r.left_up.y, r.width(), r.height())
}
