package com.ndsl.ndhs.manager

import com.ndsl.ndhs.NDHS
import com.ndsl.ndhs.encoder.TimeLine
import java.io.File

class TimeLineManager(val ndhs: NDHS) {
    /**
     * This method doesn't return empty list
     */
    fun load(file: File): List<TimeLine>? {
        println("Loading TimeLine:${file.absolutePath}")
        if (!file.exists()) throw NDHSTimeLineLoaderError("File:${file.absolutePath} does not exist")
        val timelines = ndhs.manager.timeLineLoaders.list.mapNotNull {
            it.getProjectLoader().onLoad(file)
        }
        if(timelines.isEmpty()){
            return null
        }
        return timelines
    }
}

class NDHSTimeLineLoaderError(str: String) : Throwable(str)
