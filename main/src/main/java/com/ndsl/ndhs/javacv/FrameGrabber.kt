package com.ndsl.ndhs.javacv

import com.github.bun133.nngraphics.display.Rect
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.FrameGrabber
import org.bytedeco.javacv.OpenCVFrameGrabber
import java.io.File

abstract class FrameGrabber : AutoCloseable {
    var index = 0
    abstract fun getFrameGrabber(): FrameGrabber
    fun grab(): Frame? {
        index++
        return aGrab()
    }

    fun grabImage(): Frame? {
        index++
        return aGrabImage()
    }

    fun grabAudio(): Frame? {
        index++
        return aGrabAudio()
    }

    fun set(index: Int) {
        this.index = index
        aSet(index)
    }

    fun rect() = Rect(0,0,getFrameGrabber().imageWidth,getFrameGrabber().imageHeight)

    protected abstract fun aGrab(): Frame?
    protected abstract fun aGrabImage(): Frame?
    protected abstract fun aGrabAudio(): Frame?
    protected abstract fun aSet(index: Int)
}

class FFmpegFrameGrabber(file: File) : com.ndsl.ndhs.javacv.FrameGrabber() {
    val ffmpeg = FFmpegFrameGrabber(file)
    override fun getFrameGrabber(): FrameGrabber = ffmpeg
    override fun aGrab(): Frame? = ffmpeg.grab()
    override fun aGrabImage(): Frame? = ffmpeg.grabImage()
    override fun aGrabAudio(): Frame? = ffmpeg.grabSamples()
    override fun aSet(index: Int) {
        ffmpeg.frameNumber = index
    }

    override fun close() {
        ffmpeg.close()
    }
}

class OpenCVFrameGrabber(file: File) : com.ndsl.ndhs.javacv.FrameGrabber() {
    val opencv = OpenCVFrameGrabber(file)
    override fun getFrameGrabber(): FrameGrabber = opencv
    override fun aGrab(): Frame = opencv.grab()
    override fun aGrabImage(): Frame {
        throw Exception("Not Supported in OpenCVFrameGrabber")
    }

    override fun aGrabAudio(): Frame {
        throw Exception("Not Supported in OpenCVFrameGrabber")
    }

    override fun aSet(index: Int) {
        opencv.frameNumber = index
    }

    override fun close() {
        opencv.close()
    }
}