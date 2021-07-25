package com.ndsl.ndhs.ex

import com.github.bun133.nngraphics.display.*
import java.awt.Frame
import java.awt.event.WindowEvent
import javax.swing.JFrame

fun <T> Mouse<T>.register(
    r: Rect,
    type: List<Mouse.Type>?,
    f: (Mouse.Type, T) -> Unit
): SimpleRectMouseBoundedListener<T> {
    val l = SimpleRectMouseBoundedListener(r, type, f)
    listeners.add(l)
    return l
}

class SimpleRectMouseBoundedListener<T>(var r: Rect, val type: List<Mouse.Type>?, val f: (Mouse.Type, T) -> Unit) :
    MouseBoundedListener<T> {
    override var isIn: Boolean = false
    override fun bound(): Rect = r
    override fun onInBound(p: Pos, t: Mouse.Type, event: T) {
        if (type != null && type.contains(t)) {
            f(t, event)
        }
    }

    override fun onOutBound(p: Pos, t: Mouse.Type, event: T) {
    }

    override fun type(): List<Mouse.Type>? = type
}

fun JFrameDisplay.minimise() {
    jframe.state = Frame.ICONIFIED
}

fun JFrameDisplay.maximise() {
    jframe.state = Frame.MAXIMIZED_BOTH
    jframe.extendedState = JFrame.MAXIMIZED_BOTH
}

fun JFrameDisplay.isMax() = jframe.state == Frame.MAXIMIZED_BOTH || jframe.extendedState == JFrame.MAXIMIZED_BOTH

fun JFrameDisplay.normal() {
    jframe.state = Frame.NORMAL
    jframe.extendedState = JFrame.NORMAL
}

fun JFrameDisplay.isNormal() = jframe.state == Frame.NORMAL || jframe.extendedState == JFrame.NORMAL

fun JFrameDisplay.close() {
    jframe.dispatchEvent(WindowEvent(jframe, WindowEvent.WINDOW_CLOSING))
}

fun JFrameDisplay.setBound(r: Rect) {
    jframe.setBounds(r.left_up.x, r.left_up.y, r.width(), r.height())
}