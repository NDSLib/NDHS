package com.ndsl.ndhs.javacv

import com.github.bun133.nngraphics.display.Rect
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.FrameRecorder
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.image.BufferedImage
import java.io.File

abstract class FrameRecorder {
    var index = 0
        set(value) {
            field = value
            aSetIndex(value)
        }
        get() {
            val i = aGetIndex()
            field = i
            return i
        }


    var fps = 0.0
        set(value) {
            field = value
            getFrameRecorder().frameRate = value
        }
        get() {
            val i = getFrameRecorder().frameRate
            field = i
            return i
        }

    fun record(f: Frame) {
        aRecord(f)
    }

    fun recordSamples(buf: Array<java.nio.Buffer>) {
        aRecordSamples(buf)
    }


    abstract fun start()
    abstract fun stop()
    abstract fun getFrameRecorder(): FrameRecorder
    abstract fun aSetIndex(value: Int)
    abstract fun aGetIndex(): Int
    abstract fun aRecord(f: Frame)
    abstract fun aRecordSamples(buf: Array<java.nio.Buffer>)
}

class FFmpegFrameRecorder(val file: File, val r: Rect, val audioChannels: Int) : com.ndsl.ndhs.javacv.FrameRecorder() {
    val recorder = FFmpegFrameRecorder(file, r.width(), r.height(), audioChannels)
    override fun start() {
        recorder.start()
    }

    override fun stop() {
        recorder.stop()
    }

    override fun getFrameRecorder(): FrameRecorder = recorder
    override fun aSetIndex(value: Int) {
        recorder.frameNumber = value
    }

    override fun aGetIndex(): Int = recorder.frameNumber

    override fun aRecord(f: Frame) {
        recorder.record(f)
    }

    override fun aRecordSamples(buf: Array<java.nio.Buffer>) {
        recorder.recordSamples(*buf)
    }
}

class BufferedImageRecorder(val file: File, val r: Rect, val audioChannels: Int) {
    init {
        while (true) {
            while (!buffer.isEmpty()) {
                val en = buffer.getAndRemove(0)!!
                if (en.audio != null) {
                    recorder.recordSamples(en.audio)
                } else if (en.img != null) {
                    val f = converter.getFrame(en.img)
                    recorder.record(f)
                } else {
                    println("[NDHS]While Recording,An Error Frame Occurred.In BufferedImageRecorder")
                }
            }
            Thread.sleep(1)
        }
    }

    class Entry(val img: BufferedImage?, val audio: Array<java.nio.Buffer>?)

    val recorder = FFmpegFrameRecorder(file, r, audioChannels)
    val converter = Java2DFrameConverter()
    val buffer = Buffer<Entry>()

    fun start() {
        recorder.start()
    }

    fun stop() {
        recorder.stop()
    }

    fun getIndex() = recorder.aGetIndex()
    fun setIndex(i: Int) = recorder.aSetIndex(i)
    fun record(img: BufferedImage) {
        buffer.add(Entry(img, null))
    }

    fun recordAll(vararg img: BufferedImage) {
        img.forEach {
            buffer.add(Entry(it, null))
        }
    }

    fun recordSamples(buf: Array<java.nio.Buffer>) {
        buffer.add(Entry(null, buf))
    }

    fun recordSamplesAll(vararg buf: Array<java.nio.Buffer>) {
        buf.forEach {
            buffer.add(Entry(null, it))
        }
    }

    fun isComplete() = buffer.isEmpty()
}