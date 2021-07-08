package com.ndsl.ndhs.util

import com.github.bun133.nngraphics.display.Rect
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import java.awt.font.GlyphVector

import java.awt.font.FontRenderContext




fun Graphics.drawRect(r: Rect) {
    drawRect(r.left_up.x, r.left_up.y, r.width(), r.height())
}

fun Graphics.stringBound(str: String): Rect {
//    val r = Rect(this.fontMetrics.getStringBounds(str, this).bounds)
//    val height = fontMetrics.height
//    val width = fontMetrics.stringWidth(str)
//    return Rect(0,0,width + 2,height + 2)
//    return Rect(fontMetrics.getStringBounds(str,this))

    //これが一番近そう
    val frc: FontRenderContext = (this as Graphics2D).fontRenderContext
    val gv: GlyphVector = this.getFont().createGlyphVector(frc, str)
    return Rect(gv.getPixelBounds(null, 0f, 0f))
}

fun Graphics.fillRect(r: Rect) {
    fillRect(r.left_up.x, r.left_up.y, r.width(), r.height())
}
