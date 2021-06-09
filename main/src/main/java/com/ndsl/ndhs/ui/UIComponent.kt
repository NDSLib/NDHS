package com.ndsl.ndhs.ui

import com.github.bun133.nngraphics.display.GraphicsDrawable
import com.github.bun133.nngraphics.display.Pos
import com.github.bun133.nngraphics.display.Rect
import com.ndsl.ndhs.util.StaticNamed
import java.awt.Graphics

/**
 * UIのベース、たぶんRectのほうがいいよね。
 */

abstract class UIComponent(id: String) : GraphicsDrawable(), StaticNamed {
    override val name = id
    var r = Rect(0, 0, 0, 0)
    override fun onDraw(gg: Graphics) {
        onDraw(gg, pos())
    }
    abstract fun onDraw(gg: Graphics, p: Pos)
    override fun pos(): Pos = r.left_up
    override fun setPos(pos: Pos) {
        r.shift(pos.x - pos().x, pos.y - pos().y)
    }
}