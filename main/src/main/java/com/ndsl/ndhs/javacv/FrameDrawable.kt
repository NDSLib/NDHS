package com.ndsl.ndhs.javacv

import com.github.bun133.nngraphics.display.GraphicsDrawable
import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.Graphics
import java.awt.image.BufferedImage
import kotlin.concurrent.thread
import kotlin.math.roundToLong

class FrameDrawable(val grabber: FrameGrabber) : GraphicsDrawable() {
    var img: BufferedImage? = null
    private val converter = Java2DFrameConverter()
    private var buf: SizedBuffer<BufferedImage> = SizedBuffer(20)
    var isPlaying = true
    var timer = Timer(getNs())

    init {
        thread {
            while (isPlaying) {
                if (!buf.isFilled()) {
                    buf.add(converter.getBufferedImage(grabber.grabImage()))
                }
                Thread.sleep(1)
            }
        }
    }

    override fun onDraw(gg: Graphics) {
        if(!timer.isStarted()) timer.start()
        if (img != null) {
            gg.drawImage(img, 0, 0, null)
        }

        if (timer.isUp()) {
            if (!buf.isEmpty()) {
                img = buf.getAndRemove(0)
                timer.restart()
            }
        }
    }

    private fun getNs(): Long {
        return ((1000L * 1000L * 1000L) / grabber.getFrameGrabber().frameRate).roundToLong()
    }
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
}

class Timer(var nanos: Long) {
    var startTime: Long? = null
    fun start() : Timer{
        startTime = System.nanoTime()
        return this
    }

    fun isUp(): Boolean {
        if(startTime==null) return false
        return (System.nanoTime() - startTime!!) >= nanos
    }

    fun isStarted() = startTime != null

    // 実際には過ぎた時間を考慮するStart
    fun restart(){
        if(isStarted()){
            if(isUp()){
                startTime = startTime!! + nanos
            }else{
                start()
            }
        }
    }
}