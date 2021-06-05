package com.ndsl.ndhs.plugin

import com.ndsl.ndhs.NDHS
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.net.URL
import java.net.URLClassLoader

class PluginLoader {
    companion object {
        const val PluginConfigFileName = "ndhs_plugin.txt"
        const val PluginClassConfigKey = "PluginMain"
        const val PluginVersionConfigKey = "Version"
        const val PluginNameConfigKey = "Name"
    }

    val plugins = mutableListOf<NDHSPlugin>()

    fun load(file: File, configLoader: ConfigLoader, ndhs: NDHS): NDHSPlugin {
        if (!file.endsWith(".jar")) throw NDHSPluginLoadingError("Plugin File is not JAR")
        val loader = URLClassLoader(arrayOf(file.toURI().toURL()), ClassLoader.getSystemClassLoader())
        val configURL =
            loader.getResource(PluginConfigFileName) ?: throw NDHSPluginLoadingError("PluginConfig not found!")
        val parser = ConfigParser(configURL)
        val pluginClassName = parser.getOrThrow(PluginClassConfigKey)
        val pluginName = parser.getOrThrow(PluginNameConfigKey)
        val pluginVersion = parser.getOrThrow(PluginVersionConfigKey)
        configLoader.onLoad(parser)

        // Init
        val clazz = Class.forName(pluginClassName, true, loader)
        val ins = clazz.getConstructor(NDHS::class.java).newInstance(ndhs)
        if (ins is NDHSPlugin) {
            if (!ins.onRegister()) throw NDHSPluginLoadingError("${pluginName}(Version:${pluginVersion}) is crashed during onRegister")
            plugins.add(ins)
            return ins
        } else {
            throw NDHSPluginLoadingError("Plugin File is not Plugin")
        }
    }
}

class ConfigParser(configURL: URL) {
    companion object {
        const val Splitter = ':'
    }

    val map = mutableMapOf<String, String>()

    init {
        val reader = BufferedReader(FileReader(File(configURL.toURI())))
        reader.forEachLine {
            if (it.contains(Splitter)) {
                map[it.substring(0, it.indexOf(Splitter))] = it.substring(it.indexOf(Splitter) + 1, it.lastIndex)
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
