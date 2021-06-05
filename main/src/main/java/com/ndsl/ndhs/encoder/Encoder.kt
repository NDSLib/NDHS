package com.ndsl.ndhs.encoder

import com.ndsl.ndhs.util.Named
import java.io.File

/**
 * Encoder本体
 */
abstract class Encoder : Named() {
    abstract fun encode(outFile: File, timeLine: TimeLine)
}