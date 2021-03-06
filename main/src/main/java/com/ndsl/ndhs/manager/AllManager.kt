package com.ndsl.ndhs.manager

import com.ndsl.ndhs.NDHS
import com.ndsl.ndhs.cache.ClipCacheManager
import com.ndsl.ndhs.easing.EasingManager
import com.ndsl.ndhs.icon.IconManager
import com.ndsl.ndhs.mode.ModeManager
import com.ndsl.ndhs.plugin.*

/**
 * いろんなクラスに散り散りになったいろんなものを集約するクラス
 */
class AllManager(val ndhs: NDHS) {
    lateinit var pluginLoader: PluginLoader
    val configLoaders = Registry<PluginConfigLoader>()
    val timeLineLoaders = Registry<PluginTimeLineLoader>()
    val tickListeners = Registry<PluginTickCallable>()
    val clipCacheManager = ClipCacheManager(ndhs)
    val easingManager = EasingManager(ndhs)
    val mode = ModeManager(ndhs)
    val icon = IconManager(ndhs)

    fun register(plugin: NDHSPlugin) {
        plugin.getConfigLoader().forEach { configLoaders.add(it) }
        plugin.getTimeLineLoader().forEach { timeLineLoaders.add(it) }
        plugin.getTickCallables().forEach { tickListeners.add(it) }
        plugin.getPluginClipCacheManager().forEach { clipCacheManager.cacher.add(it.getPluginClipCacheManager()) }
        plugin.getEasing().map { it.getEasing() }.forEach { easingManager.add(it) }
        plugin.getMode().forEach { mode.modes.add(it.get()) }
    }

    fun registerAll(pluginLoader: PluginLoader) {
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