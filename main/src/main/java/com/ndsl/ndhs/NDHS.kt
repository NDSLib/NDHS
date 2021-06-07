package com.ndsl.ndhs

import com.github.bun133.nngraphics.display.JFrameDisplay
import com.github.bun133.nngraphics.display.Rect
import com.ndsl.ndhs.manager.AllManager
import com.ndsl.ndhs.plugin.NDHSPlugin
import com.ndsl.ndhs.plugin.PluginLoader
import java.io.File

fun main() {
    val ndhs = NDHS()
}

class NDHS {
    companion object {
        val PluginFolder = File("main\\src\\main\\resources\\plugins")
        val guiWindows = mutableListOf<Pair<JFrameDisplay,NDHSPlugin?>>()
    }

    val manager: AllManager = AllManager()
    val pluginLoader = PluginLoader()
        .also { manager.pluginLoader = it }
        .also { it.loadAll(PluginFolder, this) }
        .also { manager.registerAll(it) }
    val drawer = NDHSDrawer(this)


    fun getPlugins(containDisabled: Boolean = false) = manager.getPlugins(containDisabled)

    /**
     * GUI Windowを簡単に生成できます(推奨)
     * @param plugin Pluginからアクセスしているときはそのインスタンス(nullはSystemからのアクセス)
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun newGUIWindow(bound:Rect, plugin:NDHSPlugin?):JFrameDisplay{
        val d =JFrameDisplay(bound = bound)
        guiWindows.add(Pair(d,plugin))
        return d
    }
}