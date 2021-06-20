package com.ndsl.ndhs.manager

import com.ndsl.ndhs.plugin.*

/**
 * いろんなクラスに散り散りになったいろんなものを集約するクラス
 */
class AllManager {
    lateinit var pluginLoader:PluginLoader
    val configLoaders = Registry<PluginConfigLoader>()
    val timeLineLoaders = Registry<PluginTimeLineLoader>()
    val tickListeners = Registry<PluginTickCallable>()
    val clipCacheManagers = Registry<PluginClipCacheManager>()

    fun register(plugin:NDHSPlugin){
        plugin.getConfigLoader().forEach { configLoaders.add(it) }
        plugin.getTimeLineLoader().forEach { timeLineLoaders.add(it) }
        plugin.getTickCallables().forEach { tickListeners.add(it) }
        plugin.getPluginClipCacheManager().forEach { clipCacheManagers.add(it) }
    }

    fun registerAll(pluginLoader:PluginLoader){
        pluginLoader.plugins.forEach { register(it) }
    }


    fun getPlugins(containDisabled: Boolean = false): List<NDHSPlugin> {
        return if (containDisabled) {
            pluginLoader.plugins
        } else pluginLoader.plugins.filter { it.isEnabled() }
    }
}

class Registry<T> {
    val list = mutableListOf<T>()
    fun add(t: T) {
        list.add(t)
    }

    fun remove(t: T) {
        list.remove(t)
    }

    fun count() = list.size
}