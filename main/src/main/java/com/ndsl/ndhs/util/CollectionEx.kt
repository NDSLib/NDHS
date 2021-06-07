package com.ndsl.ndhs.util

fun <T,R> Iterable<T>.mapList(func:(T)->List<R>): MutableList<R>{
    val r = mutableListOf<R>()
    for (e in this) func(e).forEach { r.add(it) }
    return r
}