package com.ndsl.ndhs.filter

import com.github.bun133.nngraphics.display.Rect
import java.awt.image.BufferedImage

abstract class Filter<T> {
    abstract fun filter(t: T): T
}

/**
 * このソースはほんとに信用していいのだろうか
 */
class RectFilter(val r: Rect) : Filter<BufferedImage>() {
    override fun filter(t: BufferedImage): BufferedImage {
        val buf = BufferedImage(r.width(), r.height(), t.type)
        for (x in r.left_up.x..r.right_down.x) {
            if (x <= 0 || t.width < x) continue // 範囲外
            for (y in r.left_up.y..r.right_down.y) {
                if (y <= 0 || t.height < y) continue // 範囲外
                buf.setRGB(x - r.left_up.x + 1, y - r.left_up.y + 1, t.getRGB(x, y))
            }
        }
        return buf
    }
}