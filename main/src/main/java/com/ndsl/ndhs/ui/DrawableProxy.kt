package com.ndsl.ndhs.ui

import com.github.bun133.nngraphics.display.Drawable
import com.github.bun133.nngraphics.display.NGraphic
import com.github.bun133.nngraphics.display.Pos
import com.github.bun133.nngraphics.display.Rect
import java.awt.Color

/**
 * 丸っと動かしたい時ってあるよね。
 */
class DrawableDrawable(var drawables: MutableList<Drawable>) : Drawable {
    var shift: Pos = Pos(0, 0)

    override fun onDraw(g: NGraphic) {
        drawables.forEach {
            it.onDraw(ShiftedGraphics(g, shift.x, shift.y))
        }
    }

    override fun pos(): Pos = shift
    override fun setPos(pos: Pos) {
        shift = pos
    }
}

class ShiftedGraphics(val g: NGraphic, var xShift: Int, var yShift: Int) : NGraphic {
    override fun getColor(): Color = g.getColor()
    override fun getRGB(pos: Pos): Color = g.getRGB(pos.shift(xShift, yShift))
    override fun line(from: Pos, to: Pos) {
        g.line(from.shift(xShift, yShift), to.shift(xShift, yShift))
    }

    override fun rect(rect: Rect, isFill: Boolean) {
        g.rect(rect.shift(xShift, yShift), isFill)
    }

    override fun setColor(c: Color) = g.setColor(c)
    override fun setRGB(pos: Pos, rgb: Color) = g.setRGB(pos.shift(xShift, yShift), rgb)
}

fun Pos.shift(x: Int, y: Int): Pos {
    return Pos(this.x + x, this.y + y)
}

fun Rect.shift(x: Int, y: Int): Rect {
    return Rect(this.left_up.shift(x, y), this.right_down.shift(x, y))
}