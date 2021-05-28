package com.ndsl.ndhs.javacv

import com.github.bun133.nngraphics.display.*
import com.ndsl.ndhs.sound.ByteAudioOut
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.sound.sampled.AudioFormat
import kotlin.concurrent.thread
import kotlin.math.roundToLong

// Almost Same as Frame,but easier one
class PlayerEntry(var img: BufferedImage?, var audio: Array<java.nio.Buffer>?, var frame: Frame) {
}

class Player(val grabber: FrameGrabber) : GraphicsDrawable() {
    companion object {
        private fun getAudioFormat(f: FrameGrabber): AudioFormat? {
            val fg = f.getFrameGrabber()
            var af: AudioFormat? = null
            when (fg.sampleFormat) {
                avutil.AV_SAMPLE_FMT_U8 -> {
                }
                avutil.AV_SAMPLE_FMT_S16 -> af =
                    AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED, fg.sampleRate.toFloat(), 16, fg.audioChannels,
                        fg.audioChannels * 2, fg.sampleRate.toFloat(), true
                    )
                avutil.AV_SAMPLE_FMT_S32 -> {
                }
                avutil.AV_SAMPLE_FMT_FLT -> af =
                    AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED, fg.sampleRate.toFloat(), 16, fg.audioChannels,
                        fg.audioChannels * 2, fg.sampleRate.toFloat(), true
                    )
                avutil.AV_SAMPLE_FMT_DBL -> {
                }
                avutil.AV_SAMPLE_FMT_U8P -> {
                }
                avutil.AV_SAMPLE_FMT_S16P -> af =
                    AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED, fg.sampleRate.toFloat(), 16, fg.audioChannels,
                        fg.audioChannels * 2, fg.sampleRate.toFloat(), true
                    )
                avutil.AV_SAMPLE_FMT_S32P ->                                         // 32 bit
                    af = AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED, fg.sampleRate.toFloat(), 32, fg.audioChannels,
                        fg.audioChannels * 2, fg.sampleRate.toFloat(), true
                    )
                avutil.AV_SAMPLE_FMT_FLTP -> af =
                    AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED, fg.sampleRate.toFloat(), 16, fg.audioChannels,
                        fg.audioChannels * 2, fg.sampleRate.toFloat(), true
                    )
                avutil.AV_SAMPLE_FMT_DBLP -> {
                }
                avutil.AV_SAMPLE_FMT_S64 -> {
                }
                avutil.AV_SAMPLE_FMT_S64P -> {
                }
                else -> {
                    println("unsupported")
//                exitProcess(0)
                }
            }
            return af
        }
    }


    var img: BufferedImage? = null
    private val converter = Java2DFrameConverter()
    private var imgBuf: SizedBuffer<BufferedImage> = SizedBuffer((grabber.getFrameGrabber().frameRate * 10).toInt())
    private var audioBuf: SizedBuffer<Array<java.nio.Buffer>> =
        SizedBuffer((grabber.getFrameGrabber().frameRate * 10).toInt())
    var isPlaying = true
    var drawTimer = Timer(getDrawNs())
    var audioTimer = Timer(getAudioNs())
    val audio = ByteAudioOut(getAudioFormat(grabber)!!)

    init {
        thread {
            while (true) {
                if (isPlaying && grabber.index <= grabber.getFrameGrabber().lengthInFrames) {
                    if (!imgBuf.isFilled() || !audioBuf.isFilled()) {
                        try {
                            val f = grabber.grab()
                            if (f.types.contains(Frame.Type.VIDEO)) {
                                val i = converter.getBufferedImage(f)
                                if (i != null) imgBuf.add(i)
                            } else if (f.types.contains(Frame.Type.AUDIO)) {
                                audioBuf.add(f.samples!!)
                            }

                        } catch (e: Exception) {
                            // 握りつぶします。
                            println("握りつぶしました")
                        }
                    }
                }

                if (audioTimer.isUp()) {
                    if (!audioBuf.isEmpty()) {
                        println("[Delta:${System.nanoTime() - lastAudio},Timer:${audioTimer.nanos}]Updating Audio")
                        lastAudio = System.nanoTime()
                        val short = audioBuf.getAndRemove(0)
                        if (short != null) {
                            playAudio(short)
                            audioTimer.restart()
                        }
                    } else {
                        println("Audio:Out Of Buffer")
                    }
                }
                Thread.sleep(1)
            }
        }

        audio.start()
    }

    var executor: ExecutorService = Executors.newSingleThreadExecutor()
    var lastDraw = 0L
    var lastAudio = 0L

    override fun onDraw(gg: Graphics) {
        if (!drawTimer.isStarted()) drawTimer.start()
        if (!audioTimer.isStarted()) audioTimer.start()
        if (img != null) {
            gg.drawImage(img, p.x, p.y, null)
        }

        if (drawTimer.isUp()) {
            if (!imgBuf.isEmpty()) {
                println("[Delta:${System.nanoTime() - lastDraw},Timer:${drawTimer.nanos}]Updating Image")
                lastDraw = System.nanoTime()
                val bufferedImage = imgBuf.getAndRemove(0)
                if (bufferedImage != null) {
                    img = bufferedImage
                    drawTimer.restart()
                }
            } else {
                println("Image:Out Of Buffer")
            }
        }
    }

    private fun playAudio(b: ShortBuffer) {
        b.rewind()

        val outBuffer = ByteBuffer.allocate(b.capacity() * 2)

        for (i in 0 until b.capacity()) {
            val value: Short = b.get(i)
            outBuffer.putShort(value)
        }

        executor.submit {
            audio.write(outBuffer.array(), 0, outBuffer.capacity())
            outBuffer.clear()
        }.get()
    }

    private fun playAudio(samples: Array<java.nio.Buffer>) {
        var k: Int
        val buf: Array<java.nio.Buffer> = samples
        val leftData: FloatBuffer
        val rightData: FloatBuffer
        val ILData: ShortBuffer
        val IRData: ShortBuffer
        val TLData: ByteBuffer
        val TRData: ByteBuffer
        val vol = 0.1f // volume
        val tl: ByteArray
        val tr: ByteArray
        val combine: ByteArray
        when (grabber.getFrameGrabber().sampleFormat) {
            avutil.AV_SAMPLE_FMT_FLTP -> {
                leftData = buf[0] as FloatBuffer
                TLData = floatToByteValue(leftData, vol)
                rightData = buf[1] as FloatBuffer
                TRData = floatToByteValue(rightData, vol)
                tl = TLData.array()
                tr = TRData.array()
                combine = ByteArray(tl.size + tr.size)
                k = 0
                var i = 0
                while (i < tl.size) {
                    //Mix two channels
                    var j = 0
                    while (j < 2) {
                        combine[j + 4 * k] = tl[i + j]
                        combine[j + 2 + 4 * k] = tr[i + j]
                        j++
                    }
                    k++
                    i += 2
                }
                audio.write(combine, 0, combine.size)
            }
            avutil.AV_SAMPLE_FMT_S16 -> {
                ILData = buf[0] as ShortBuffer
                TLData = shortToByteValue(ILData, vol)
                tl = TLData.array()
                audio.write(tl, 0, tl.size)
            }
            avutil.AV_SAMPLE_FMT_FLT -> {
                leftData = buf[0] as FloatBuffer
                TLData = floatToByteValue(leftData, vol)
                tl = TLData.array()
                audio.write(tl, 0, tl.size)
            }
            avutil.AV_SAMPLE_FMT_S16P -> {
                ILData = buf[0] as ShortBuffer
                IRData = buf[1] as ShortBuffer
                TLData = shortToByteValue(ILData, vol)
                TRData = shortToByteValue(IRData, vol)
                tl = TLData.array()
                tr = TRData.array()
                combine = ByteArray(tl.size + tr.size)
                k = 0
                var i = 0
                while (i < tl.size) {
                    var j = 0
                    while (j < 2) {
                        combine[j + 4 * k] = tl[i + j]
                        combine[j + 2 + 4 * k] = tr[i + j]
                        j++
                    }
                    k++
                    i += 2
                }
                audio.write(combine, 0, combine.size)
            }
            else -> {
                println("unsupport audio format")
//                System.exit(0)
            }
        }
    }

    private fun shortToByteValue(arr: ShortBuffer, vol: Float): ByteBuffer {
        val len = arr.capacity()
        val bb = ByteBuffer.allocate(len * 2)
        for (i in 0 until len) {
            bb.putShort(i * 2, (arr[i].toFloat() * vol).toInt().toShort())
        }
        return bb
    }

    private fun floatToByteValue(arr: FloatBuffer, vol: Float): ByteBuffer {
        val len = arr.capacity()
        var f: Float
        val v: Float
        val res = ByteBuffer.allocate(len * 2)
        v = 32768.0f * vol
        for (i in 0 until len) {
            f =
                arr[i] * v
            if (f > v) f = v
            if (f < -v) f = v
            res.putShort(i * 2, f.toInt().toShort())
        }
        return res
    }

    var p = Pos(0, 0)
    override fun pos(): Pos = p
    override fun setPos(pos: Pos) {
        p = pos
    }

    fun setFrame(i: Int) {
        pause()
        imgBuf.clear()
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

    private fun getDrawNs(): Long {
        return ((1000L * 1000L * 1000L) / grabber.getFrameGrabber().frameRate).roundToLong()
    }

    private fun getAudioNs(): Long {
        return ((1000L * 1000L * 1000L) / grabber.getFrameGrabber().sampleRate)
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