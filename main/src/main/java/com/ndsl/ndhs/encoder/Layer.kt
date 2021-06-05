package com.ndsl.ndhs.encoder

abstract class Layer {
    abstract fun getContents():MutableList<Content>
}