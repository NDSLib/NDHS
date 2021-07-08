package com.ndsl.ndhs.mode

import com.github.bun133.nngraphics.display.Scene

class Streaming : Mode() {
    var isInit = false
    var s = Scene()
    override fun getScene(): Scene = s
    override fun onChanged() {
        println("Mode changed to Streaming Mode")
        if (!isInit) {
            println("Streaming Mode is now init...")
            i()
            println("Streaming Mode Init is End!")
        }
    }

    override val name: String = "Steaming"
    fun i() {
        // INIT
    }
}