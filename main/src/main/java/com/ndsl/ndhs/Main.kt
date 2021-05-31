package com.ndsl.ndhs

import com.github.bun133.nngraphics.display.JFrameDisplay
import com.github.bun133.nngraphics.display.Pos
import com.github.bun133.nngraphics.display.Rect
import com.ndsl.ndhs.javacv.*
import org.bytedeco.javacv.Frame
import java.io.File
import kotlin.concurrent.thread
import kotlin.time.measureTime

fun main() {
//    Main().main()
//    BufferTest().test()
    DoublingTest().main2()
}

class DoublingTest {
    companion object {
        val Infile = File("main\\src\\main\\resources\\video1.mp4")
        val Outfile = File("main\\src\\main\\resources\\video1_copied.mp4")
    }

    val inputBuffer = Buffer<Frame>()

    var isLoaded = false

    fun main() {
        val grabber = FFmpegFrameGrabber(Infile)
        grabber.getFrameGrabber().start()
        val recorder = FFmpegFrameRecorder(Outfile, grabber.rect(), grabber.getFrameGrabber().audioChannels)
        recorder.getFrameRecorder().start()

        thread {
            val len = grabber.getFrameGrabber().lengthInFrames
            println("Start Loading...")
            val ls = System.nanoTime()
            while (grabber.index < len) {
                inputBuffer.add(grabber.grab()!!) // Error Here
            }
            val le = System.nanoTime()
            println("End Loading...")
            println("Loading:${le - ls}ns")
            isLoaded = true
        }

        while (!isLoaded) {
            Thread.sleep(100)
        }

        thread {
            println("Start Recording...")
            val rs = System.nanoTime()
            while (!inputBuffer.isEmpty()) {
                recorder.record(inputBuffer.getAndRemove(0)!!)
            }
            val re = System.nanoTime()
            println("End Recording...")
            println("Recording:${re - rs}ns")
        }

        println("Closing...")
        grabber.close()
        recorder.getFrameRecorder().close()
    }

    fun main2() {
        if (Outfile.exists()) {
            println("OutFile Exists,Deleting...")
            Outfile.delete()
        }

        val start = System.nanoTime()
        val grabber = FFmpegFrameGrabber(Infile)
        grabber.getFrameGrabber().start()
        val recorder = FFmpegFrameRecorder(Outfile, grabber.rect(), grabber.getFrameGrabber().audioChannels)
        recorder.fps = grabber.getFrameGrabber().frameRate
        println("Fps Set to ${recorder.fps}")
        recorder.getFrameRecorder().videoBitrate = grabber.getFrameGrabber().videoBitrate
        println("Video Bitrate Set to ${recorder.getFrameRecorder().videoBitrate}")
        recorder.getFrameRecorder().audioBitrate = grabber.getFrameGrabber().audioBitrate
        println("Audio Bitrate Set to ${recorder.getFrameRecorder().audioBitrate}")
        recorder.getFrameRecorder().videoCodec = grabber.getFrameGrabber().videoCodec
        println("Video Codec Set to ${recorder.getFrameRecorder().videoCodec}")
        recorder.getFrameRecorder().audioCodec = grabber.getFrameGrabber().audioCodec
        println("audioCodec Set to ${recorder.getFrameRecorder().audioCodec}")

        recorder.getFrameRecorder().start()

        val len = grabber.getFrameGrabber().lengthInFrames
        var current = 0
        var wasNull = false
        do {
            current = grabber.getFrameGrabber().frameNumber
            if (current % 100 == 0) {
                println("Pending... ${current}/$len ${100.0 * current / len}%")
            }
            val f = grabber.grab()
            if (f != null) {
                recorder.record(f)
            } else {
                wasNull = true
            }
        } while (!wasNull)

        println("Closing...")
        println("Length:${grabber.getFrameGrabber().lengthInTime},${grabber.getFrameGrabber().lengthInFrames}frames Video was doubled in ${System.nanoTime() - start}ns")
        grabber.close()
        recorder.getFrameRecorder().close()

    }
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
    val grabber = FFmpegFrameGrabber(File("main\\src\\main\\resources\\video2.mp4"))
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