package com.ndsl.ndhs.ui

import com.github.bun133.nngraphics.display.*
import com.ndsl.ndhs.NDHSDisplay
import com.ndsl.ndhs.font.FontBuilder
import com.ndsl.ndhs.font.defaultFontName
import com.ndsl.ndhs.ui.color.UIColorSet
import com.ndsl.ndhs.util.StaticNamed
import com.ndsl.ndhs.util.drawRect
import com.ndsl.ndhs.util.fillRect
import com.ndsl.ndhs.util.stringBound
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.geom.Rectangle2D

/**
 * UIのベース、たぶんRectのほうがいいよね。
 */

abstract class UIComponent(id: String, rr: Rect) : GraphicsDrawable(), StaticNamed {
    override val name = id
    private var r = rr
    override fun onDraw(gg: Graphics) {
        onDraw(gg, rect())
    }

    abstract fun onDraw(gg: Graphics, r: Rect)
    override fun pos(): Pos = r.left_up
    override fun setPos(pos: Pos) {
        r.shift(pos.x - pos().x, pos.y - pos().y)
    }

    fun rect() = r
    abstract val color: UIColorSet
}

abstract class PositionableUIComponent(id: String, rr: Rect, style: UIPositionStyle) : UIComponent(id, rr)

enum class UIPositionStyle {
    // 中心寄り表示
    Center,

    // 左側表示
    Left,

    // 右側表示
    Right,

    // 指定なし
    None
}


class Label(id: String, rr: Rect, val f: Font, t: String, val style: UIPositionStyle = UIPositionStyle.None) :
    PositionableUIComponent(id, rr, style) {

    class LabelColorSet(val l: Label) : UIColorSet(l) {
        // 文字の色
        override var mainColor: Color = Color.BLACK
    }

    constructor(
        id: String,
        rr: Rect,
        text: String,
        fontSize: Int,
        style: UIPositionStyle = UIPositionStyle.None
    ) : this(
        id,
        rr,
        Font(defaultFontName, 0, fontSize),
        text,
        style
    )

    constructor(id: String, rr: Rect, text: String, fBuilder: FontBuilder) : this(id, rr, fBuilder.build(), text)

    var text = t
    private var lastTimeText = ""
    private var lastTimeRect = Rect(0, 0, 0, 0)
    private var lastTimeCenterDrawRect = Rect(0, 0, 0, 0)
    private var lastTimeRightDrawRect = Rect(0, 0, 0, 0)
    override val color: UIColorSet = LabelColorSet(this)

    override fun onDraw(gg: Graphics, r: Rect) {
        updateRect(gg, r)
        gg.font = f
        gg.color = color.mainColor
        when (style) {
            UIPositionStyle.Center -> {
                // むずくね。
                // そうでもなじかった。
                gg.drawString(text, lastTimeCenterDrawRect.left_up.x, lastTimeCenterDrawRect.right_down.y)
            }

            UIPositionStyle.None -> {
                gg.drawString(text, r.left_up.x, lastTimeCenterDrawRect.right_down.y)
            }

            UIPositionStyle.Left -> {
                gg.drawString(text, r.left_up.x, lastTimeCenterDrawRect.right_down.y)
            }

            UIPositionStyle.Right -> {
                gg.drawString(text, lastTimeRightDrawRect.left_up.x, lastTimeCenterDrawRect.right_down.y)
            }
        }
    }

    private fun updateRect(gg: Graphics, r: Rect) {
        if (lastTimeText != text) {
            // Rect Update
            lastTimeRect = gg.stringBound(text)
            lastTimeCenterDrawRect = lastTimeRect.shift(
                r.center().x - lastTimeRect.center().x,
                r.center().y - lastTimeRect.center().y
            )
            lastTimeRightDrawRect = lastTimeRect.shift(
                r.right_down.x - lastTimeRect.right_down.x,
                r.left_up.y - lastTimeRect.left_up.y
            )
            lastTimeText = text
        }
    }
}

class Button(
    val label: Label,
    id: String,
    style: UIPositionStyle = UIPositionStyle.None,
    rr: Rect = label.rect(),
    display: NDHSDisplay
) :
    PositionableUIComponent(id, rr, style) {

    init {
        display.jFrame.mouse.listeners.add(MouseListener(this))
    }

    class ButtonColorSet(val b: Button) : UIColorSet(b) {
        // 文字色
        override var mainColor: Color = Color.BLACK

        // 枠の色
        override var accentColor: Color = Color.BLACK

        // 背景色
        override var backGroundColor: Color = Color.LIGHT_GRAY

        // 押された時の色
        override var actionColor: Color = Color.CYAN
    }

    class MouseListener(val b: Button, override var isIn: Boolean = false) : MouseBoundedListener<MouseEvent> {
        override fun bound(): Rect = b.rect()
        override fun on(p: Pos, t: Mouse.Type, event: MouseEvent) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (t) {
                Mouse.Type.Release -> {
                    b.isClicking = false
                    b.clicked()
                }
                Mouse.Type.LeftClick -> {
                    b.isClicking = true
                    b.clicking()
                }
                Mouse.Type.Exit ->{
                    b.isClicking = false
                }
            }
        }

        override fun type(): List<Mouse.Type> = mutableListOf(Mouse.Type.LeftClick, Mouse.Type.Release,Mouse.Type.Exit)
    }

    override val color: UIColorSet = ButtonColorSet(this)
    var isClicking = false
    override fun onDraw(gg: Graphics, r: Rect) {
        if (isClicking) {
            gg.color = color.actionColor
        } else {
            gg.color = color.backGroundColor
        }
        gg.fillRect(r)
        gg.color = color.accentColor
        gg.drawRect(r)
        label.color.mainColor = color.mainColor
        label.onDraw(gg)
    }

    open fun clicked(){
        println("Clicked")
    }
    open fun clicking(){
        println("Clicking")
    }
}

