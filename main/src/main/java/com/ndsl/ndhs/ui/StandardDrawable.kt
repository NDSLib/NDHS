package com.ndsl.ndhs.ui

import com.github.bun133.nngraphics.display.*
import java.awt.Color
import java.awt.image.BufferedImage

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

class FPSMeasure(d: JFrameDisplay) : Drawable {
    init {
        d.windowListener.registry.register({
            val end = System.nanoTime()
            println("Closing...")
            println("---- FPS Report ----")
            println("Frames:${frames}")
            println("Time:${end - startTime}ns")
            println("FPN:${frames.toDouble() / (end - startTime)}")
            println("FPS:${frames / ((end - startTime) / (1000L * 1000L * 1000L))}")
        }, WinListener.Type.Closing)
    }

    var startTime = 0L
    var frames = 0L

    override fun onDraw(g: NGraphic) {
        if (startTime == 0L) {
            startTime = System.nanoTime()
        }
        frames++
    }

    override fun pos(): Pos = Pos(0, 0)
    override fun setPos(pos: Pos) {}
}

class DImg(var img: BufferedImage, pos: Pos) : PosedDrawable(pos) {
    override fun onDraw(g: NGraphic) {
        if (g is GraphicsWrapper) {
            draw(g)
        } else if (g is ShiftedGraphics && g.g is GraphicsWrapper) {
            draw(g.g)
        }
    }

    private fun draw(g: GraphicsWrapper) {
        g.graphics.drawImage(img, pos().x, pos().y, null)
    }
}