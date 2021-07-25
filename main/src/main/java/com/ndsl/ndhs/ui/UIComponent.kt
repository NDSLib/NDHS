package com.ndsl.ndhs.ui

import com.github.bun133.nngraphics.display.*
import com.ndsl.ndhs.NDHSDisplay
import com.ndsl.ndhs.font.FontBuilder
import com.ndsl.ndhs.font.defaultFontName
import com.ndsl.ndhs.ui.color.UIColorSet
import com.ndsl.ndhs.util.*
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.event.MouseEvent

/**
 * UIのベース、たぶんRectのほうがいいよね。
 */

abstract class UIComponent(id: String, rr: Rect) : GraphicsDrawable(), StaticNamed {
    override val name = id
    protected var r = rr
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
    open var isVisible: Boolean = true
        set(value) {
            field = value
            child().forEach { it.isVisible = value }
        }

    fun child() = combineMutableList(before(),after())
    open fun after():MutableList<UIComponent>? = null
    open fun before():MutableList<UIComponent>? = null

    /**
     * 引数にDisplayとか見たくないです。
     */
    open fun onAdded(display:NDHSDisplay){}
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
        if (!isVisible) return
        gg.font = f
        // Font適用してなかった...
        updateRect(gg, r)
        gg.color = color.mainColor
        when (style) {
            UIPositionStyle.Center -> {
                // むずくね。
                // そうでもなじかった。
                gg.drawString(
                    text,
                    lastTimeCenterDrawRect.left_up.x,
                    avg(
                        lastTimeCenterDrawRect.right_down.y,
                        lastTimeCenterDrawRect.left_up.y
                    ) + lastTimeCenterDrawRect.height() / 2
                )
            }

            UIPositionStyle.None -> {
                gg.drawString(
                    text,
                    r.left_up.x,
                    avg(
                        lastTimeCenterDrawRect.right_down.y,
                        lastTimeCenterDrawRect.left_up.y
                    ) + lastTimeCenterDrawRect.height() / 2
                )
            }

            UIPositionStyle.Left -> {
                gg.drawString(
                    text,
                    r.left_up.x,
                    avg(
                        lastTimeCenterDrawRect.right_down.y,
                        lastTimeCenterDrawRect.left_up.y
                    ) + lastTimeCenterDrawRect.height() / 2
                )
            }

            UIPositionStyle.Right -> {
                gg.drawString(
                    text,
                    lastTimeRightDrawRect.left_up.x,
                    avg(
                        lastTimeCenterDrawRect.right_down.y,
                        lastTimeCenterDrawRect.left_up.y
                    ) + lastTimeCenterDrawRect.height() / 2
                )
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
    rr: Rect = label.rect()
) :
    PositionableUIComponent(id, rr, style) {

    override fun onAdded(display: NDHSDisplay) {
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

    override fun after(): MutableList<UIComponent> = mutableListOf(label)

    class MouseListener(val b: Button, override var isIn: Boolean = false) : MouseBoundedListener<MouseEvent> {
        override fun bound(): Rect = b.rect()
        override fun onInBound(p: Pos, t: Mouse.Type, event: MouseEvent) {
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
                Mouse.Type.Exit -> {
                    b.isClicking = false
                }
            }
        }
        override fun onOutBound(p: Pos, t: Mouse.Type, event: MouseEvent) = Unit
        override fun type(): List<Mouse.Type> = mutableListOf(Mouse.Type.LeftClick, Mouse.Type.Release, Mouse.Type.Exit)
    }

    override val color: UIColorSet = ButtonColorSet(this)
    var isClicking = false
    override fun onDraw(gg: Graphics, r: Rect) {
        if (!isVisible) return
        if (isClicking) {
            gg.color = color.actionColor
        } else {
            gg.color = color.backGroundColor
        }
        gg.fillRect(r)
        gg.color = color.accentColor
        gg.drawRect(r)
        label.color.mainColor = color.mainColor
    }

    open fun clicked() {
        onClickListener.forEach { it(this) }
    }

    open fun clicking() {
        onClickingListener.forEach { it(this) }
    }

    private val onClickListener = mutableListOf<(Button) -> Unit>()
    private val onClickingListener = mutableListOf<(Button) -> Unit>()
    fun onClick(f: (Button) -> Unit) = onClickListener.add(f)
    fun onClicking(f: (Button) -> Unit) = onClickingListener.add(f)
}

// ドロワー
class Drawer<T>(
    val button: Button,
    id: String,
    rr: Rect = button.rect(),
    style: UIPositionStyle = UIPositionStyle.Left
) : PositionableUIComponent(id, rr, style) {
    init {
        button.isVisible = false
        button.onClick {
            onClick()
        }
    }

    class DrawerColorSet(comp: UIComponent) : UIColorSet(comp) {
    }

    override fun onDraw(gg: Graphics, r: Rect) {

    }

    override val color: UIColorSet = DrawerColorSet(this)

    fun onClick(){

    }
}