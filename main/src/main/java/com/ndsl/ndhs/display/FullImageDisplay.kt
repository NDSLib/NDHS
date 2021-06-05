package com.ndsl.ndhs.display

import com.github.bun133.nngraphics.display.JFrameDisplay
import com.github.bun133.nngraphics.display.Pos
import com.github.bun133.nngraphics.display.Rect
import com.ndsl.ndhs.ui.DImg
import java.awt.image.BufferedImage

class FullImageDisplay(val img: BufferedImage, val left_up: Pos) {
    val display = JFrameDisplay(bound = Rect(left_up, Pos(left_up.x + img.width, left_up.y + img.height)),isUndecorated = true)

    init {
        display.scene().newLayer().add(DImg(img, Pos(0,0)))
    }
}