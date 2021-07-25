package com.ndsl.ndhs.display.basic

import com.github.bun133.nngraphics.display.Mouse
import com.github.bun133.nngraphics.display.Pos
import com.github.bun133.nngraphics.display.Rect
import com.ndsl.ndhs.NDHSDisplay
import com.ndsl.ndhs.ex.*
import com.ndsl.ndhs.ex.around
import com.ndsl.ndhs.ui.UIComponent
import com.ndsl.ndhs.ui.color.UIColorSet
import com.ndsl.ndhs.ui.shift
import java.awt.Graphics
import java.awt.event.MouseEvent

class HangBar(id: String, rr: Rect, val ndhsDisplay: NDHSDisplay) : UIComponent(id, rr) {
    companion object {
        const val Height = 30
        const val Width = 30
    }

    val click = ndhsDisplay.jFrame.mouse.register(
        ndhsDisplay.jFrame.rect(),
        listOf(Mouse.Type.Drag)
    ) { t, e -> drag(t, e) }

    override fun onDraw(gg: Graphics, r: Rect) {
        val windowR = ndhsDisplay.jFrame.rect()
        this.r = Rect(0, 0, windowR.width(), Height)
        gg.color = color.backGroundColor
        rect().fill(gg)
        click.r = windowR
    }

    override fun after(): MutableList<UIComponent> = mutableListOf(Minimise(this), Maximise(this), Close(this))

    class Color(comp: UIComponent) : UIColorSet(comp) {
        override var mainColor = java.awt.Color.WHITE
    }

    override val color = Color(this)

    fun minimise() {
        ndhsDisplay.jFrame.minimise()
    }

    fun maximise() {
        if (ndhsDisplay.jFrame.isMax()) {
            ndhsDisplay.jFrame.normal()
        } else {
            ndhsDisplay.jFrame.maximise()
        }
    }

    fun close() {
        ndhsDisplay.jFrame.close()
    }

    var last: Pos? = null
    fun drag(t: Mouse.Type, e: MouseEvent) {
        if (t == Mouse.Type.Drag) {
            if (last != null) {
                val r = ndhsDisplay.jFrame.rectOnDisplay().shift(e.xOnScreen - last!!.x, e.yOnScreen - last!!.y)
                ndhsDisplay.jFrame.setBound(r)
            }
            last = Pos(e.xOnScreen, e.yOnScreen)
        }
    }

    class Minimise(val hangBar: HangBar) : UIComponent(hangBar.name + "min", hangBar.rect()) {
        val click = hangBar.ndhsDisplay.jFrame.mouse.register(
            rect(),
            listOf(Mouse.Type.LeftClick)
        ) { _, _ -> hangBar.minimise() }

        override fun onDraw(gg: Graphics, r: Rect) {
            val hangRect = hangBar.rect()
            this.r = Rect(
                hangRect.right_down.x - HangBar.Width * 3,
                hangRect.left_up.y,
                hangRect.right_down.x - HangBar.Width * 2,
                hangRect.left_up.y + HangBar.Height
            )

            gg.color = color.backGroundColor
            rect().fill(gg)
            gg.color = color.mainColor
            var line = rect()
            line = Rect(
                line.left_up.x + HangBar.Width / 4,
                line.left_up.y + HangBar.Height / 2,
                line.right_down.x - HangBar.Width / 4,
                line.right_down.y - HangBar.Height / 2
            )
            line.line(gg)

            click.r = rect()
        }

        override val color = hangBar.color
    }

    class Maximise(val hangBar: HangBar) : UIComponent(hangBar.name + "max", hangBar.rect()) {
        val click = hangBar.ndhsDisplay.jFrame.mouse.register(
            rect(),
            listOf(Mouse.Type.LeftClick)
        ) { _, _ -> hangBar.maximise() }


        override fun onDraw(gg: Graphics, r: Rect) {
            val hangRect = hangBar.rect()
            this.r = Rect(
                hangRect.right_down.x - HangBar.Width * 2,
                hangRect.left_up.y,
                hangRect.right_down.x - HangBar.Width * 1,
                hangRect.left_up.y + HangBar.Height
            )

            gg.color = color.backGroundColor
            rect().fill(gg)
            var iconR = rect()
            iconR = Rect(
                iconR.left_up.x + HangBar.Width / 4,
                iconR.left_up.y + HangBar.Height / 4,
                iconR.right_down.x - HangBar.Width / 4,
                iconR.right_down.y - HangBar.Height / 4
            )
            iconR.around(gg, color.mainColor, color.backGroundColor)

            click.r = rect()
        }

        override val color = hangBar.color
    }

    class Close(val hangBar: HangBar) : UIComponent(hangBar.name + "close", hangBar.rect()) {
        val click = hangBar.ndhsDisplay.jFrame.mouse.register(
            rect(),
            listOf(Mouse.Type.LeftClick)
        ) { _, _ -> hangBar.close() }


        override fun onDraw(gg: Graphics, r: Rect) {
            val hangRect = hangBar.rect()
            this.r = Rect(
                hangRect.right_down.x - HangBar.Width * 1,
                hangRect.left_up.y,
                hangRect.right_down.x,
                hangRect.left_up.y + HangBar.Height
            )
            gg.color = color.backGroundColor
            rect().fill(gg)
            var iconR = rect()
            iconR = Rect(
                iconR.left_up.x + HangBar.Width / 4,
                iconR.left_up.y + HangBar.Height / 4,
                iconR.right_down.x - HangBar.Width / 4,
                iconR.right_down.y - HangBar.Height / 4
            )
            gg.color = color.mainColor
            iconR.cross(gg)

            click.r = rect()
        }

        override val color = hangBar.color
    }
}