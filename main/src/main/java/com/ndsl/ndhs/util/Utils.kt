package com.ndsl.ndhs.util

fun avg(vararg i:Int): Int {
    var b = 0
    for (l in i) b += l
    return b / i.size
}

fun avg(vararg d:Double): Double {
    var b = 0.0
    for (l in d) b += l
    return b / d.size
}