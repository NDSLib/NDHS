package com.ndsl.ndhs.plugin

import com.ndsl.ndhs.NDHS
import com.ndsl.ndhs.encoder.Encoder
import com.ndsl.ndhs.encoder.Filter
import com.ndsl.ndhs.util.Named

/**
 * Plugin本体クラス
 * これをOverrideさせる
 */
abstract class NDHSPlugin(val ndhs:NDHS) : Named() {
    /**
     * 成功したならTrueを返そうね。
     */
    abstract fun onRegister(): Boolean
    abstract fun isEnabled(): Boolean
    abstract fun getAll(): MutableList<PluginContent>
    abstract fun getFilters(): MutableList<PluginFilter>
    abstract fun getEncoder(): MutableList<PluginEncoder>
    abstract fun getConfigLoader(): MutableList<PluginConfigLoader>
}

/**
 * Pluginが返すものの大本
 */
abstract class PluginContent() {
    abstract fun getPlugin(): NDHSPlugin
}

/**
 * PluginContentの中でFilterを返すもの
 */
abstract class PluginFilter : PluginContent() {
    abstract fun getFilter(): Filter
}

/**
 * PluginContentの中でEncoderを返すもの
 */
abstract class PluginEncoder : PluginContent() {
    abstract fun getEncoder(): Encoder
}

abstract class PluginConfigLoader : PluginContent() {
    abstract fun getConfigLoader(): PluginConfigLoader
}