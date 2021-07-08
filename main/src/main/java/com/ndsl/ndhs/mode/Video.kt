package com.ndsl.ndhs.mode

import com.github.bun133.nngraphics.display.Scene

class Video: Mode() {
    var isInit = false
    var s = Scene()
    override fun getScene(): Scene = s

    override fun onChanged() {
        println("Mode changed to Video Mode")
        if(!isInit){
            println("Video Mode is now init...")
            i()
            println("Video Mode Init is End!")
        }
    }

    override val name: String = "Video"

    fun i(){
        // INIT
    }
}