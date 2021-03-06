package com.ndsl.ndhs

import com.github.bun133.nngraphics.display.Rect
import com.ndsl.ndhs.manager.AllManager
import com.ndsl.ndhs.plugin.NDHSPlugin
import com.ndsl.ndhs.plugin.PluginLoader
import com.ndsl.ndhs.plugin.PluginTickCallable
import java.io.File
import javax.swing.JFrame

fun main() {
    val ndhs = NDHS()
    ndhs.drawer.invokeDrawThread()

    ndhs.tickManager.listeners().add(DebugCallable())
    while (true) {
        ndhs.tickManager.tick()
    }
}

class DebugCallable : PluginTickCallable(), ITickCallable {
    override fun getTickCallable(): ITickCallable {
        return this
    }

    override fun getPlugin(): NDHSPlugin? = null
    override fun onTick(ndhs: NDHS, tickCount: Long) {
//        println("Tick[${tickCount}] Plugins:${ndhs.getPlugins(true).size}")
    }
}

class NDHS {
    companion object {
        val PluginFolder = File("main\\src\\main\\resources\\plugins")
        val guiWindows = mutableListOf<Pair<NDHSDisplay, NDHSPlugin?>>()
    }


    val drawer = NDHSDrawer(this)
    val manager: AllManager = AllManager(this)
    private val pluginLoader = PluginLoader(this)
        .also { manager.pluginLoader = it }
        .also { it.loadAll(PluginFolder) }
        .also { manager.registerAll(it) }
    val tickManager = TickManager(this, pluginLoader)


    // Methods
    fun getPlugins(containDisabled: Boolean = false) = manager.getPlugins(containDisabled)
    fun getPlugin(name: String, containDisabled: Boolean = false) =
        getPlugins(containDisabled).filter { it.name() == name }

    /**
     * GUI Windowを簡単に生成できます(推奨)
     * @param plugin Pluginからアクセスしているときはそのインスタンス(nullはSystemからのアクセス)
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun newGUIWindow(
        bound: Rect,
        plugin: NDHSPlugin?,
        exitOnClose: Boolean = false,
        isUnDecorated: Boolean = false
    ): NDHSDisplay {
        val d = if (!exitOnClose) {
            NDHSDisplay(bound = bound, closeOperation = JFrame.DISPOSE_ON_CLOSE, isUndecorated = isUnDecorated)
        } else {
            NDHSDisplay(bound = bound, closeOperation = JFrame.EXIT_ON_CLOSE, isUndecorated = isUnDecorated)
        }
        guiWindows.add(Pair(d, plugin))
        return d
    }

    fun getWorkingFolder() = File("")
}