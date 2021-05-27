package com.ndsl.ndhs

import com.github.bun133.nngraphics.display.JFrameDisplay
import com.github.bun133.nngraphics.display.Pos
import com.github.bun133.nngraphics.display.Rect
import com.ndsl.ndhs.javacv.Buffer
import com.ndsl.ndhs.javacv.FFmpegFrameGrabber
import com.ndsl.ndhs.javacv.Player
import com.ndsl.ndhs.javacv.PlayerSeekBar
import java.io.File

fun main() {
    Main().main()
//    BufferTest().test()
}

class BufferTest {
    fun test() {
        val buffer = Buffer<Int>()
        for (i in 0..10) buffer.add(i)
        var t: Int? = null
        do {
            t = buffer.getAndRemove(0)
            println(t)
        } while (t != null)
    }
}

class Main {
    val d = JFrameDisplay(name = "NDHS", bound = Rect(100, 100, 900, 900))
    val grabber = FFmpegFrameGrabber(File("main\\src\\main\\resources\\video1.mp4"))
    fun main() {
        grabber.getFrameGrabber().start()
        val player = PlayerSeekBar(Player(grabber))
        d.scene().newLayer().add(player)
        d.mouse.listeners.add(player)
        while (true) {
            d.draw.update()
//            player.setFrame(100)
        }
    }
}