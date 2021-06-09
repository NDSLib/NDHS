package com.ndsl.ndhs.plugin

import com.ndsl.ndhs.NDHS
import java.io.*
import java.net.URLClassLoader

class PluginLoader {
    companion object {
        const val PluginConfigFileName = "ndhs_plugin.txt"
        const val PluginClassConfigKey = "PluginMain"
        const val PluginVersionConfigKey = "Version"
        const val PluginNameConfigKey = "Name"
    }

    val plugins = mutableListOf<NDHSPlugin>()

    fun load(file: File, ndhs: NDHS): NDHSPlugin {
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
            ins.getConfigLoader().map { it.getConfigLoader() }.forEach { it.onLoad(parser) }
            if (!ins.onRegister()) throw NDHSPluginLoadingError("${pluginName}(Version:${pluginVersion}) is crashed during onRegister")
            plugins.add(ins)
            return ins
        } else {
            throw NDHSPluginLoadingError("Plugin File is not Plugin")
        }
    }

    fun loadAll(folder: File, ndhs: NDHS): MutableList<NDHSPlugin> {
        if (!folder.exists()) {
            folder.mkdir()
        }
        if (folder.isDirectory) {
            return folder.listFiles()!!.map { load(it, ndhs) }.toMutableList()
        } else {
            throw NDHSPluginLoadingError("loadAll,Not Correct:${folder.absolutePath}")
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
}

class NDHSPluginLoadingError(s: String) : Throwable(s)
