package com.ndsl.ndhs.ui

import com.github.bun133.nngraphics.display.Drawable
import com.github.bun133.nngraphics.display.NGraphic
import com.github.bun133.nngraphics.display.Pos
import com.github.bun133.nngraphics.display.Rect
import java.awt.Color

abstract class PosedDrawable(pos: Pos) : Drawable {
    private var p: Pos = pos
    override fun pos(): Pos = p
    override fun setPos(pos: Pos) {
        p = pos
    }

    fun shift(p: Pos): Pos = p.shift(-this.p.x, -this.p.y)
    fun shift(r: Rect): Rect = r.shift(-this.p.x, -this.p.y)
}

class DLine(var from: Pos, var to: Pos, var c: Color = Color.BLACK, pos: Pos = Pos(0, 0)) : PosedDrawable(pos) {
    override fun onDraw(g: NGraphic) {
        g.setColor(c)
        g.line(shift(from), shift(to))
//        g.line(from, to)
    }
}

class DRect(var r: Rect, var isFilled: Boolean = false, var c: Color = Color.BLACK, pos: Pos) : PosedDrawable(pos) {
    override fun onDraw(g: NGraphic) {
        g.setColor(c)
        g.rect(shift(r), isFilled)
    }
}