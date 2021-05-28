package com.ndsl.ndhs.sound

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine

class ByteAudioOut(val format: AudioFormat) {
    val info = DataLine.Info(SourceDataLine::class.java, format)
    val line = AudioSystem.getLine(info) as SourceDataLine

    fun start() {
        line.open(format)
        line.start()
    }

    fun write(buf: ByteArray) {
        line.write(buf,0,buf.size)
    }

    fun write(buf: ByteArray,off:Int,len:Int) {
        line.write(buf,off,len)
    }

    fun close() {
        line.drain()
        line.stop()
        line.close()
    }
}