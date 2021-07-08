package com.ndsl.ndhs.easing

import com.github.bun133.nngraphics.display.Pos
import com.github.bun133.nngraphics.display.Rect
import com.ndsl.ndhs.ui.Label
import com.ndsl.ndhs.ui.UIComponent
import com.ndsl.ndhs.ui.UIPositionStyle
import com.ndsl.ndhs.ui.color.UIColorSet
import com.ndsl.ndhs.util.fillRect
import java.awt.Color
import java.awt.Graphics

class DoubleEasingDrawable(id: String, rr: Rect, val easing: DoubleEasing) : UIComponent(id, rr) {
    class Color(comp: UIComponent) : UIColorSet(comp) {
        override var backGroundColor = java.awt.Color.GRAY

        // 文字カラー
        override var mainColor = java.awt.Color.WHITE

        // 線の色
        override var actionColor = java.awt.Color.BLUE
    }

    override val color = Color(this)
    val label =
        Label(
            id + "_label",
            Rect(rr.left_up.x, rr.left_up.y, rr.right_down.x, rr.left_up.y + 40),
            easing.name,
            12,
            UIPositionStyle.Center
        )

    override fun after(): MutableList<UIComponent> = mutableListOf(label)
    override fun onDraw(gg: Graphics, r: Rect) {
        gg.color = color.backGroundColor
        gg.fillRect(r)
        draw(gg, Rect(r.left_up.x, r.left_up.y + 40, r.right_down.x, r.right_down.y))
    }

    fun draw(g: Graphics, r: Rect) {
        (0..r.right_down.x - r.left_up.x).forEach {
            g.color = color.actionColor
            val d = it.toDouble().map(.0, (r.right_down.x - r.left_up.x).toDouble(), .0, easing.length())
            val ease = easing.getAt(d)
            val y = (r.right_down.y - ease * r.height()).toInt()
//            println("X:${r.left_up.x + it},I:${it},D:$d,EASE:${ease},EASETYPE:${easing.name},Y:$y")
            g.drawPos(
                Pos(
                    r.left_up.x + it,
                    y
                )
            )
        }
    }
}

private fun Graphics.drawPos(pos: Pos) {
    drawLine(pos.x, pos.y, pos.x, pos.y)
}

/**
 * 数値をマップする。
 * @param min この値がとる最小の範囲
 * @param max この値がとる最大の範囲
 * @param rmin 出力値がとる最小の範囲
 * @param rmax 出力値がとる最大の範囲
 */
fun Double.map(min: Double, max: Double, rmin: Double, rmax: Double): Double {
    return (this - min) * (rmax - rmin) / (max - min) + rmin
}

fun main() {
    println(5.0.map(0.0, 10.0, .0, 1.0))
    println(5.0.map(5.0, 10.0, .0, 1.0))
    println(5.0.map(0.0, 5.0, .0, 1.0))
    println(5.0.map(0.0, 5.0, .5, 1.0))
}