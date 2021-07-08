package com.ndsl.ndhs.easing

import com.ndsl.ndhs.NDHS
import com.ndsl.ndhs.manager.Registry
import com.ndsl.ndhs.util.StaticNamed
import kotlin.math.*

// イージング
// TはIntとかFloatとかとか
abstract class Easing<T> : StaticNamed {
    abstract fun getAt(t: T): T
    abstract fun length(): T
}

class FloatEasing(val f: (Float) -> Float, n: String, private val len: Float) : Easing<Float>() {
    override fun getAt(t: Float): Float = f(t)
    override val name: String = n
    override fun length(): Float = len
}

class IntEasing(val f: (Int) -> Int, n: String, private val len: Int) : Easing<Int>() {
    override fun getAt(t: Int): Int = f(t)
    override val name: String = n
    override fun length(): Int = len
}

class DoubleEasing(val f: (Double) -> Double, n: String, private val len: Double) : Easing<Double>() {
    override fun getAt(t: Double): Double = f(t)
    override val name: String = n
    override fun length(): Double = len
}

class EasingManager(val ndhs: NDHS) {
    val float: Registry<FloatEasing> = Registry()
    val int: Registry<IntEasing> = Registry()
    val double: Registry<DoubleEasing> = Registry()

    fun add(e: Easing<*>) {
        when (e) {
            is FloatEasing -> {
                add(e)
            }
            is IntEasing -> {
                add(e)
            }
            is DoubleEasing -> {
                add(e)
            }
            else -> {
                throw Exception("In EasingManager,not supported Easing is added:$e")
            }
        }
    }

    fun add(e: FloatEasing) = float.add(e)
    fun add(e: IntEasing) = int.add(e)
    fun add(e: DoubleEasing) = double.add(e)

    init {
        DefaultEasingGenerator(this) // Default追加
    }
}

class DefaultEasingGenerator(easingManager: EasingManager) {
    init {
        i(easingManager)
    }

    fun i(easingManager: EasingManager) {
        Int.all.forEach { easingManager.add(it) }
        Float.all.forEach { easingManager.add(it) }
        Double.all.forEach { easingManager.add(it) }
    }

    class Int {
        companion object {
            val simple = IntEasing({ it }, "Simple", 100)
            val all = mutableListOf(simple)
        }
    }

    class Float {
        companion object {
            val all = mutableListOf<FloatEasing>()
        }
    }

    class Double {
        companion object {
            val easeInSine = DoubleEasing({ 1 - cos((it * PI) / 2) }, "easeInSine", 1.0)
            val easeOutSine = DoubleEasing({ sin((it * PI) / 2) }, "easeOutSine", 1.0)
            val easeInOutSine = DoubleEasing({ -(cos(PI * it) - 1) / 2 }, "easeInOutSine", 1.0)
            val easeInQuad = DoubleEasing({ it * it }, "easeInQuad", 1.0)
            val easeOutQuad = DoubleEasing({ 1 - (1 - it) * (1 - it) }, "easeOutQuad", 1.0)
            val easeInOutQuad = DoubleEasing({
                if (it < 0.5) {
                    2 * it * it
                } else {
                    1 - (-2 * it + 2).pow(2) / 2
                }
            }, "easeInOutQuad", 1.0)
            val easeInCubic = DoubleEasing({ it * it * it }, "easeInCubic", 1.0)
            val easeOutCubic = DoubleEasing({ 1 - easeInCubic.f(it) }, "easeOutCubic", 1.0)
            val easeInOutCubic = DoubleEasing({
                if (it < 0.5) {
                    4 * easeInCubic.f(it)
                } else {
                    1 - (-2 * it + 2).pow(3) / 2
                }
            }, "easeInOutCubic", 1.0)
            val easeInQuart = DoubleEasing({ it * it * it * it }, "easeInQuart", 1.0)
            val easeOutQuart = DoubleEasing({ 1 - (1 - it).pow(4) }, "easeOutQuart", 1.0)
            val easeInOutQuart = DoubleEasing({
                if (it < 0.5) {
                    8 * it * it * it * it
                } else {
                    1 - (-2 * it + 2).pow(4) / 2
                }
            }, "easeInOutQuart", 1.0)

            val easeInQuint = DoubleEasing({ it * it * it * it * it }, "easeInQuint", 1.0)
            val easeOutQuint = DoubleEasing({ 1 - (1 - it).pow(5) }, "easeOutQuint", 1.0)
            val easeInOutQuint = DoubleEasing({
                if (it < 0.5) {
                    16 * it.pow(5)
                } else {
                    1 - (-2 * it + 2).pow(5) / 2
                }
            }, "easeInOutQuint", 1.0)

            val easeInExpo = DoubleEasing({ if (it == 0.0) 0.0 else 2.0.pow(10 * it - 10) }, "easeInExpo", 1.0)
            val easeOutExpo = DoubleEasing({ if (it == 1.0) 1.0 else 1.0 - 2.0.pow(-10 * it) }, "easeOutExpo", 1.0)
            val easeInOutExpo = DoubleEasing({
                when {
                    it == 0.0 -> 0.0
                    it == 1.0 -> 1.0
                    it < 0.5 -> 2.0.pow(20 * it - 10) / 2
                    else -> (2.0 - 2.0.pow(-20 * it + 10)) / 2
                }
            }, "easeInOutExpo", 1.0)

            val easeInCirc = DoubleEasing({ 1 - sqrt(1 - it * it) }, "easeInCirc", 1.0)
            val easeOutCirc = DoubleEasing({ sqrt(1 - (it - 1).pow(2)) }, "easeOutCirc", 1.0)
            val easeInOutCirc = DoubleEasing({
                if (it < 0.5) {
                    (1 - sqrt(1 - (2 * it).pow(2))) / 2
                } else {
                    (sqrt(1 - (-2 * it + 2).pow(2)) + 1) / 2
                }
            }, "easeInOutCirc", 1.0)

            val easeInBack = DoubleEasing({
                (1.70158 + 1) * it * it * it - 1.70158 * it * it
            }, "easeInBack", 1.0)
            val easeOutBack = DoubleEasing({
                1 + (1.70158 + 1) * (it - 1).pow(3) + 1.70158 * (it - 1).pow(2)
            }, "easeOutBack", 1.0)
            val easeInOutBack = DoubleEasing({
                if (it < 0.5) {
                    ((2 * it).pow(2) * ((1.70158 * 1.525 + 1) * 2 * it - 1.70158 * 1.525)) / 2
                } else {
                    ((2 * it - 2).pow(2) * ((1.70158 * 1.525 + 1) * (it * 2 - 2) + 1.70158 * 1.525) + 2) / 2
                }
            }, "easeInOutBack", 1.0)

            val all = mutableListOf(
                easeInSine, easeOutSine, easeInOutSine,
                easeInQuad, easeOutQuad, easeInOutQuad,
                easeInCubic, easeOutCubic, easeInOutCubic,
                easeInQuart, easeOutQuart, easeInOutQuart,
                easeInQuint, easeOutQuint, easeInOutQuint,
                easeInExpo, easeOutExpo, easeInOutExpo,
                easeInCirc, easeOutCirc, easeInOutCirc,
                easeInBack, easeOutBack, easeInOutBack,
            )
        }
    }
}