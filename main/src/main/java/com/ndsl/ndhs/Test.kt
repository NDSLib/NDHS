package com.ndsl.ndhs

import com.github.bun133.nngraphics.display.*
import com.ndsl.ndhs.display.FullImageDisplay
import com.ndsl.ndhs.easing.DefaultEasingGenerator
import com.ndsl.ndhs.easing.DoubleEasingDrawable
import com.ndsl.ndhs.easing.EasingManager
import com.ndsl.ndhs.javacv.*
import com.ndsl.ndhs.ui.*
import org.bytedeco.javacv.Frame
import java.io.File
import javax.imageio.ImageIO
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.time.measureTime

fun main() {
//    Main().main()
//    BufferTest().test()
//    DoublingTest().main2()
//    DrawableDrawableTest().main()
//    StandardDrawableTest().main()
//    FPSMeasureTest().main()
//    FullImageDisplayTest().main()
//    NDHSDisplayTest().main()
    EasingTest().display(100,400,7)
}

class EasingTest {
    fun display(height: Int, width: Int,mod:Int) {
        val ndhsDisplay = NDHSDisplay(bound = Rect(100, 100, 500, 500))
        DefaultEasingGenerator.Double.all.forEachIndexed { index, doubleEasing ->
            ndhsDisplay.register(
                DoubleEasingDrawable(
                    "double_easing_$index",
                    Rect(
                        0 + (index / mod) * width,
                        100 + (index % mod) * height,
                        width + (index / mod) * width,
                        100 + height + (index % mod) * height
                    ),
                    doubleEasing
                )
            )
        }

        while (true) {
            ndhsDisplay.jFrame.draw.update()
        }
    }
}

class NDHSDisplayTest {
    fun main() {
        val ndhsDisplay = NDHSDisplay(bound = Rect(100, 100, 500, 500))
        ndhsDisplay.register(
            Label(
                id = "label-counter",
                rr = Rect(100, 200, 300, 240),
                text = "Counter:0",
                fontSize = 15,
                style = UIPositionStyle.Left
            )
        )
        ndhsDisplay.register(
            Button(
                label = Label(
                    id = "label-1",
                    rr = Rect(100, 100, 300, 140),
                    text = "Text",
                    fontSize = 15,
                    style = UIPositionStyle.Center
                ),
                id = "button-1",
                display = ndhsDisplay
            )
        )
        ndhsDisplay.register(
            Button(
                label = Label(
                    id = "toggle-label",
                    rr = Rect(200, 200, 300, 240),
                    text = "Text",
                    fontSize = 15,
                    style = UIPositionStyle.Center
                ),
                id = "toggle-button",
                display = ndhsDisplay
            )
        )

        val label = ndhsDisplay.get("label-counter")!! as Label
        val button = ndhsDisplay.get("button-1")!! as Button
        val buttonText = ndhsDisplay.get("label-1")!! as Label
        val toggleButton = ndhsDisplay.get("toggle-button")!! as Button

        toggleButton.onClick {
            button.isVisible = !button.isVisible
            // レイヤー分け確認できるよ
//            println(ndhsDisplay.jFrame.scene().layers.mapIndexed{index, layer -> "Layer${index}:${layer.drawables}" })
        }

        var count = 0
        button.isVisible = false
        button.onClick {
            count++
            label.text = "Counter:$count"
        }

        while (true) {
            ndhsDisplay.jFrame.draw.update()
        }
    }
}

class FullImageDisplayTest {
    fun main() {
        val d = FullImageDisplay(ImageIO.read(File("main\\src\\main\\resources\\test.png")), Pos(100, 100))
        while (true) {
            d.display.draw.update()
        }
    }
}

class FPSMeasureTest {
    fun main() {
        val d = JFrameDisplay(bound = Rect(100, 100, 500, 500))

        d.scene().newLayer().add(FPSMeasure(d))

        while (true) {
            d.draw.update()
        }
    }
}

class StandardDrawableTest {
    fun main() {
        val d = JFrameDisplay(bound = Rect(100, 100, 500, 500))
        val ds = mutableListOf<Drawable>()
        val r = Random(158464684)
        for (i in 0..1000) {
            ds.add(DLine(Pos(r.nextInt(100), r.nextInt(100)), Pos(r.nextInt(100), r.nextInt(100))))
        }

        val layer = d.scene().newLayer()

        ds.forEach { layer.add(it) }

        while (true) {
            d.draw.update()
        }
    }
}

class DrawableDrawableTest {
    fun main() {
        val d = JFrameDisplay(bound = Rect(100, 100, 500, 500))
        val ds = mutableListOf<Drawable>()
        val r = Random(158464684)
        for (i in 0..100) {
            ds.add(DLine(Pos(r.nextInt(500), r.nextInt(500)), Pos(r.nextInt(500), r.nextInt(500))))
        }

        val dd = DrawableDrawable(ds)

        d.mouse.register.register({
            dd.setPos(Pos(it.point))
        }, Mouse.Type.Move)

        val layer = d.scene().newLayer()

        layer.add(dd)
        layer.add(FPSMeasure(d)) // FPS

        while (true) {
            d.draw.update()
        }
    }
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