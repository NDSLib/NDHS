package com.ndsl.ndhs.javacv

import com.github.bun133.nngraphics.display.*
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import kotlin.concurrent.thread
import kotlin.math.roundToLong

class Player(val grabber: FrameGrabber) : GraphicsDrawable() {
    var img: BufferedImage? = null
    private val converter = Java2DFrameConverter()
    private var buf: SizedBuffer<BufferedImage> = SizedBuffer(20)
    var isPlaying = true
    var timer = Timer(getNs())

    init {
        thread {
            while (true) {
                if (isPlaying && grabber.index <= grabber.getFrameGrabber().lengthInFrames) {
                    if (!buf.isFilled()) {
                        try {
                            val i = converter.getBufferedImage(grabber.grabImage())
                            if (i != null) buf.add(i)
                        } catch (e: Exception) {
                            // 握りつぶします。
                            println("握りつぶしました")
                        }
                    }
                }
                Thread.sleep(1)
            }
        }
    }

    override fun onDraw(gg: Graphics) {
        if (!timer.isStarted()) timer.start()
        if (img != null) {
            gg.drawImage(img, p.x, p.y, null)
        }

        if (timer.isUp()) {
            if (!buf.isEmpty()) {
                img = buf.getAndRemove(0)
                timer.restart()
            }
        }
    }

    var p = Pos(0, 0)
    override fun pos(): Pos = p
    override fun setPos(pos: Pos) {
        p = pos
    }

    fun setFrame(i: Int) {
        pause()
        buf.clear()
        grabber.set(i)
        start()
    }

    fun pause() {
        isPlaying = false
    }

    fun start() {
        isPlaying = true
    }

    fun getFrame(): Int {
        return grabber.getFrameGrabber().frameNumber
    }

    private fun getNs(): Long {
        return ((1000L * 1000L * 1000L) / grabber.getFrameGrabber().frameRate).roundToLong()
    }

    fun width() = grabber.getFrameGrabber().imageWidth
    fun height() = grabber.getFrameGrabber().imageHeight
    fun size(): Rect = Rect(0, 0, width(), height())
}

class PlayerSeekBar(val player: Player, var nonFilled: Color = Color.GRAY, var filled: Color = Color.WHITE) :
    GraphicsDrawable(), MouseBoundedListener {
    override fun onDraw(gg: Graphics) {
        player.onDraw(gg)

        drawSeekBar(gg)
    }

    override fun pos(): Pos {
        return player.pos()
    }

    override fun setPos(pos: Pos) {
        player.setPos(pos)
    }

    var seekBarHeight = 10
    var seekBarBottom = 40

    private fun drawSeekBar(gg: Graphics) {
        gg.color = nonFilled
        gg.fillRect(
            player.pos().x,
            (player.pos().y + player.height()) - (seekBarBottom + seekBarHeight),
            player.width(),
            seekBarHeight
        )
        gg.color = filled
        gg.fillRect(
            player.pos().x,
            (player.pos().y + player.height()) - (seekBarBottom + seekBarHeight),
            getFilledWidth(),
            seekBarHeight
        )
    }

    private fun getFilledWidth(): Int {
        return player.width() * player.getFrame() / player.grabber.getFrameGrabber().lengthInFrames
    }

    fun seekBarRect() = Rect(
        player.pos().x,
        (player.pos().y + player.height()) - (seekBarBottom + seekBarHeight),
        player.pos().x + player.width(),
        (player.pos().y + player.height()) - (seekBarBottom + seekBarHeight) + seekBarHeight
    )

    override fun bound(): Rect = Rect(
        player.pos().x,
        (player.pos().y + player.height()) - (seekBarBottom + seekBarHeight),
        player.pos().x + player.width(),
        (player.pos().y + player.height()) - (seekBarBottom + seekBarHeight) + (seekBarHeight + seekBarBottom)
    )

    override fun on(p: Pos, t: Mouse.Type) {
        if (seekBarRect().contain(p)) {
            // SeekBar操作
            player.setFrame(player.grabber.getFrameGrabber().lengthInFrames * (p.x - player.pos().x) / player.width())
        }
    }

    override fun type(): List<Mouse.Type> = listOf(Mouse.Type.LeftClick, Mouse.Type.Drag)
}

/*
class FrameBuffer(val grabber: FrameGrabber, var counts: Int) {
    private var buffer = mutableListOf<Frame>()
    fun onBuffer() {
        thread {
            if (buffer.size < counts) {
                for (i in buffer.size + 1..counts) {
                    buffer.add(grab())
                }
                println("FrameBuffer Filled")
            } else println("FrameBuffer is Full")
        }
    }

    private fun grab(): Frame {
        val f = grabber.grabImage()
        return if (f.types.contains(Frame.Type.VIDEO)) {
            f
        } else grab()
    }

    fun get(): Frame? {
        val buf = buffer.getOrNull(0)
        if (buf != null) {
            buffer.remove(buf)
        }
        return buf
    }

    fun getAll(): MutableList<Frame> {
        val buf = buffer
        buffer = mutableListOf()
        return buf
    }
}

class FrameConverterBuffer(val converter: Java2DFrameConverter, val grabber: FrameGrabber, var amount: Int) {
    private val buffer = mutableListOf<BufferedImage>()
    fun onBuffer() {
        if (buffer.size > amount) return
        val f = grab()
        if (f == null || f.imageChannels == 0) return
        try {
            val img = converter.getBufferedImage(f)
            if (img != null) {
                buffer.add(img)
            } else {
                println("NULL")
            }
        } catch (e: Exception) {
            // もみ消します。JavaCVが悪い。
        }
    }

    private fun grab(): Frame? {
        val f = grabber.grabImage()
        return if (f.types.contains(Frame.Type.VIDEO)) {
            f
        } else null
    }

    fun get(): BufferedImage? {
        val i = buffer.getOrNull(0)
        if (i != null) {
            buffer.remove(i)
        }
        return i
    }
}*/

class Buffer<T> {
    val list = mutableListOf<T>()
    fun add(t: T) {
        list.add(t)
    }

    fun getAndRemove(i: Int): T? {
        val t = list.getOrNull(i) ?: return null
        if (!list.remove(t)) println("Remove Failure")
        return t
    }

    fun isEmpty() = list.isEmpty()

    fun clear() {
        list.clear()
    }
}

class SizedBuffer<T>(var size: Int) {
    val buf: Buffer<T> = Buffer()
    fun add(t: T) {
        buf.add(t)
    }

    fun getAndRemove(i: Int): T? = buf.getAndRemove(i)

    fun isEmpty() = buf.isEmpty()

    fun isFilled() = (size <= buf.list.size)

    fun count() = buf.list.size

    fun clear() {
        buf.clear()
    }
}

class Timer(var nanos: Long) {
    var startTime: Long? = null
    fun start(): Timer {
        startTime = System.nanoTime()
        return this
    }

    fun isUp(): Boolean {
        if (startTime == null) return false
        return (System.nanoTime() - startTime!!) >= nanos
    }

    fun isStarted() = startTime != null

    // 実際には過ぎた時間を考慮するStart
    fun restart() {
        if (isStarted()) {
            if (isUp()) {
                startTime = startTime!! + nanos
            } else {
                start()
            }
        }
    }
}