package com.ndsl.ndhs.util

fun <T,R> Iterable<T>.mapList(func:(T)->List<R>): MutableList<R>{
    val r = mutableListOf<R>()
    for (e in this) func(e).forEach { r.add(it) }
    return r
}

fun <T> combineMutableList(vararg ts:List<T>):List<T>{
    val list = mutableListOf<T>()
    ts.forEach { list.addAll(it) }
    return list
}

@JvmName("combineMutableList1")
fun <T> combineMutableList(vararg ts:MutableList<T>?):MutableList<T>{
    val list = mutableListOf<T>()
    ts.filterNotNull().forEach { list.addAll(it) }
    return list
}