package com.ndsl.ndhs.ex

import com.github.bun133.nngraphics.display.Pos
import com.github.bun133.nngraphics.display.Rect
import com.ndsl.ndhs.util.fillRect
import java.awt.Color
import java.awt.Graphics

fun Rect.fill(g: Graphics) {
    g.fillRect(this)
}

fun Rect.line(g: Graphics) {
    g.drawLine(left_up.x, left_up.y, right_down.x, right_down.y)
}

fun Rect.around(g: Graphics, out: Color, inside: Color) {
    g.color = out
    fill(g)
    g.color = inside
    inside().fill(g)
}

fun Rect.around(g: Graphics, inside: Color) {
    around(g, g.color, inside)
}

fun Rect.inside(): Rect {
    return Rect(left_up.x + 1, left_up.y + 1, right_down.x - 1, right_down.y - 1)
}

fun Rect.cross(g: Graphics) {
    val ps = getPoints()
    Rect(ps[0], ps[3]).line(g)
    Rect(ps[1], ps[2]).line(g)
}

/**
 * @return [0]-left up,[1]-left down,[2]-right up,[3]-right down
 */
fun Rect.getPoints(): Array<Pos> {
    return arrayOf(
        Pos(left_up.x, left_up.y),
        Pos(left_up.x, right_down.y),
        Pos(right_down.x, left_up.y),
        Pos(right_down.x, right_down.y)
    )
}