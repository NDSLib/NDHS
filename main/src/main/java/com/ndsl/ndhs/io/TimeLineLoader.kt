package com.ndsl.ndhs.io

import com.ndsl.ndhs.encoder.TimeLine
import java.io.File

abstract class TimeLineLoader {
    // 複数同時追加を見越してnull-able。
    // あ、俺このファイルのローダーじゃないってときはNull返却
    abstract fun onLoad(file:File): TimeLine?
}