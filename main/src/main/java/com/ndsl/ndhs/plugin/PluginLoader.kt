package com.ndsl.ndhs.plugin

import com.ndsl.ndhs.NDHS
import java.io.*
import java.net.URLClassLoader
import java.util.*

class PluginLoader(val ndhs: NDHS) {
    companion object {
        const val PluginConfigFileName = "ndhs_plugin.txt"
        const val PluginClassConfigKey = "PluginMain"
        const val PluginVersionConfigKey = "Version"
        const val PluginNameConfigKey = "Name"
        const val PluginRequireConfigKey = "Require"
    }

    val plugins = mutableListOf<NDHSPlugin>()
    val configs = mutableMapOf<NDHSPlugin, ConfigParser>()

    fun load(file: File): NDHSPlugin {
        println("Loading Plugin File:${file.absolutePath}")
        if (!file.name.endsWith(".jar")) throw NDHSPluginLoadingError("Plugin File is not JAR")
        val loader = URLClassLoader(arrayOf(file.toURI().toURL()), ClassLoader.getSystemClassLoader())
        val configURL =
            loader.getResourceAsStream(PluginConfigFileName) ?: throw NDHSPluginLoadingError("PluginConfig not found!")
        val parser = ConfigParser(configURL)
        val pluginClassName = parser.getOrThrow(PluginClassConfigKey)
        val pluginName = parser.getOrThrow(PluginNameConfigKey)
        val pluginVersion = parser.getOrThrow(PluginVersionConfigKey)

        // Init
        val clazz = Class.forName(pluginClassName, true, loader)
        val ins = clazz.getConstructor(NDHS::class.java).newInstance(ndhs)
        if (ins is NDHSPlugin) {
            // Registerの前に読み込み
            ins.setName(pluginName) // initにsetName書きたくないでしょ?
            ins.getConfigLoader().map { it.getConfigLoader() }.forEach { it.onLoad(parser) }
            if (!ins.onRegister()) throw NDHSPluginLoadingError("${pluginName}(Version:${pluginVersion}) is crashed during onRegister")
            plugins.add(ins)
            configs[ins] = parser
            return ins
        } else {
            throw NDHSPluginLoadingError("Plugin File is not Plugin")
        }
    }

    fun loadAll(folder: File): MutableList<NDHSPlugin> {
        if (!folder.exists()) {
            folder.mkdir()
        }
        if (folder.isDirectory) {
            val l = folder.listFiles()!!.map { load(it) }.toMutableList()
            requiredPluginCheck()
            return l
        } else {
            throw NDHSPluginLoadingError("loadAll,Not Correct:${folder.absolutePath}")
        }
    }

    /**
     * 依存関係確認
     */
    fun requiredPluginCheck() {
        plugins.forEach {
            checkRequiredThrow(it)
//            if (!checkRequired(it)) {
//                throw NDHSPluginLoadingError("Required Plugin of Plugin:${it.name()} is/are missing")
//            }
        }
    }

    private fun checkRequired(it: NDHSPlugin): Boolean {
        val requirements = configs[it]!!.getArray(PluginRequireConfigKey)
        return if (requirements == null || requirements.isEmpty()) {
            // Nothing is required.
            true
        } else {
            !requirements.any { plugins.filter { p -> p.name() == it }.isEmpty() }
        }
    }

    private fun checkRequiredThrow(it: NDHSPlugin) {
        val requirements = configs[it]!!.getArray(PluginRequireConfigKey)
        if (requirements != null && requirements.isNotEmpty()) {
            val missing = requirements.filter { s -> !plugins.any { it.name() == s } }
            if (missing.isNotEmpty()) {
                throw NDHSPluginLoadingError("Required Plugin:$missing of Plugin:${it.name()} is/are missing")
            }
        }
    }
}

class ConfigParser(configStream: InputStream) {
    companion object {
        const val Splitter = ':'
    }

    val map = mutableMapOf<String, String>()

    init {
        val reader = BufferedReader(InputStreamReader(configStream))
        reader.forEachLine {
            if (it.contains(Splitter)) {
                map[it.substring(0, it.indexOf(Splitter))] = it.substring(it.indexOf(Splitter) + 1, it.lastIndex + 1)
            } else {
                throw NDHSPluginLoadingError("In ConfigParser,illegal line found")
            }
        }
    }

    fun get(key: String) = map[key]
    fun getOrThrow(key: String): String {
        val s = get(key)
        if (s == null) {
            throw NDHSPluginLoadingError("While Loading Config")
        } else {
            return s
        }
    }

    fun getArray(key: String): List<String>? {
        val s = get(key)
        if (s == null) return null
        else {
            if (s[0] == '[' && s[s.lastIndex] == ']') {
                // [] で囲われてるか
                return s.substring(1, s.lastIndex).split(',')
            } else {
                return null
            }
        }
    }

    fun getArrayOrThrow(key: String): List<String> {
        val l = getArray(key) ?: throw NDHSPluginLoadingError("While Loading Config")
        return l
    }
}

class NDHSPluginLoadingError(s: String) : Throwable(s)
