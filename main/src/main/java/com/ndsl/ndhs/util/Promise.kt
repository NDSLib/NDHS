package com.ndsl.ndhs.util

class Promise<T> {
    private var t: T? = null
    private val hooker = mutableListOf<(T) -> Unit>()
    fun success(t: T) {
        this.t = t
        hooker.forEach { it(t) }
    }

    fun fail(e: Exception) {
        throw PromiseException(e)
    }

    fun registerHook(f: (T) -> Unit) {
        hooker.add(f)
    }
}

class PromiseException(e: Exception) : Throwable("PromiseException:${e.message}")
