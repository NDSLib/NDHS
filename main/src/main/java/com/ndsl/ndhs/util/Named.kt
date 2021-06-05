package com.ndsl.ndhs.util

open class Named {
    private var name: String = "(NullName)"
    fun name(): String = name
    fun setName(s: String){
        name = s
    }
}