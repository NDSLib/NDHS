package com.ndsl.ndhs

import com.ndsl.ndhs.plugin.PluginLoader

class TickManager(val ndhs: NDHS,val pluginLoader: PluginLoader) {
    fun listeners() = ndhs.manager.tickListeners
    private var tickCount = 0L
    fun tick(){
        listeners().list.forEach {
            it.getTickCallable().onTick(ndhs,tickCount)
        }
        tickCount++
    }
}

interface ITickCallable{
    fun onTick(ndhs:NDHS,tickCount:Long)
}
