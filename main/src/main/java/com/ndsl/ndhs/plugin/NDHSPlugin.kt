package com.ndsl.ndhs.plugin

import com.ndsl.ndhs.cache.ClipCache
import com.ndsl.ndhs.ITickCallable
import com.ndsl.ndhs.NDHS
import com.ndsl.ndhs.easing.DoubleEasing
import com.ndsl.ndhs.easing.Easing
import com.ndsl.ndhs.easing.FloatEasing
import com.ndsl.ndhs.easing.IntEasing
import com.ndsl.ndhs.encoder.Encoder
import com.ndsl.ndhs.encoder.Filter
import com.ndsl.ndhs.io.TimeLineLoader
import com.ndsl.ndhs.mode.Mode
import com.ndsl.ndhs.util.Named
import java.awt.image.BufferedImage
import java.nio.Buffer

/**
 * Plugin本体クラス
 * これをOverrideさせる
 */
abstract class NDHSPlugin(val ndhs: NDHS) : Named() {
    /**
     * 成功したならTrueを返そうね。
     */
    abstract fun onRegister(): Boolean
    abstract fun isEnabled(): Boolean
    abstract fun getAll(): MutableList<PluginContent>
    abstract fun getFilters(): MutableList<PluginFilter>
    abstract fun getEncoder(): MutableList<PluginEncoder>
    abstract fun getConfigLoader(): MutableList<PluginConfigLoader>
    abstract fun getTimeLineLoader(): MutableList<PluginTimeLineLoader>
    abstract fun getTickCallables(): MutableList<PluginTickCallable>
    abstract fun getPluginClipCacheManager(): MutableList<PluginClipCacheManager>
    abstract fun getEasing(): MutableList<PluginEasing>
    abstract fun getMode(): MutableList<PluginMode>
}

/**
 * Pluginが返すものの大本
 */
abstract class PluginContent() {
    /**
     * System系はnull許容
     */
    abstract fun getPlugin(): NDHSPlugin?
}

/**
 * PluginContentの中でFilterを返すもの
 */
abstract class PluginFilter : PluginContent() {
    abstract fun getFilter(): Filter<*>

    /**
     * Video用のFilter返却
     * getAudioFilterかこれかどちらか一方がnull,もう一方にインスタンス
     */
    abstract fun getVideoFilter(): Filter<BufferedImage>?

    /**
     * Audio用のFilter返却
     * getVideoFilterかこれかどちらか一方がnull,もう一方にインスタンス
     */
    abstract fun getAudioFilter(): Filter<Array<Buffer>>?
}

/**
 * PluginContentの中でEncoderを返すもの
 */
abstract class PluginEncoder : PluginContent() {
    abstract fun getEncoder(): Encoder
}

/**
 * ConfigLoader、ConfigParserが渡される。
 * (追加プロパティとか読み込める用)
 */
abstract class PluginConfigLoader : PluginContent() {
    abstract fun getConfigLoader(): ConfigLoader
}

/**
 * ProjectLoader
 */
abstract class PluginTimeLineLoader : PluginContent() {
    abstract fun getProjectLoader(): TimeLineLoader
}

/**
 * Tick処理を登録する用
 */
abstract class PluginTickCallable : PluginContent() {
    abstract fun getTickCallable(): ITickCallable
}

abstract class PluginClipCacheManager : PluginContent() {
    abstract fun getPluginClipCacheManager(): ClipCache<*>
    abstract fun <T> getPluginClipCacheManagerTyped(t: T): ClipCache<T>
}

/**
 * Easingを提供するクラス
 */
abstract class PluginEasing : PluginContent() {
    abstract fun getEasing(): Easing<*>
    abstract fun getIntEasing(): IntEasing
    abstract fun getFloatEasing(): FloatEasing
    abstract fun getDoubleEasing(): DoubleEasing
}

/**
 * Mode追加用
 */
abstract class PluginMode : PluginContent() {
    abstract fun get(): Mode
}