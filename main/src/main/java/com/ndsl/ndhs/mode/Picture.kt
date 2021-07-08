package com.ndsl.ndhs.mode

import com.github.bun133.nngraphics.display.Scene

class Picture : Mode() {
    var isInit = false
    var s = Scene()
    override fun getScene(): Scene = s

    override fun onChanged() {
        println("Mode changed to Picture Mode")
        if (!isInit) {
            println("Picture Mode is now init...")
            i()
            println("Picture Mode Init is End!")
        }
    }

    override val name: String = "Picture"

    fun i() {
        // INIT
    }
}