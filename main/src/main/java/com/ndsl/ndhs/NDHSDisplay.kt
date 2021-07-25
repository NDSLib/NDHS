package com.ndsl.ndhs

import com.github.bun133.nngraphics.display.JFrameDisplay
import com.github.bun133.nngraphics.display.Layer
import com.github.bun133.nngraphics.display.Rect
import com.github.bun133.nngraphics.display.Scene
import com.ndsl.ndhs.display.basic.HangBar
import com.ndsl.ndhs.ui.UIComponent
import java.util.*
import javax.swing.JFrame

class NDHSDisplay(val jFrame: JFrameDisplay, val isUndecorated: Boolean) {
    /**
     * あほ長Constructor
     */
    constructor(
        name: String = "NNGraphics",
        bound: Rect,
        closeOperation: Int = JFrame.EXIT_ON_CLOSE,
        bufferSize: Int = 3,
        isUndecorated: Boolean = false
    ) : this(JFrameDisplay(name, bound, closeOperation, bufferSize, true), isUndecorated)

    val UIManager = UIManager(this)

    //    fun register(comp: UIComponent) = UIManager.register(comp)
    fun <T : UIComponent> register(comp: T): T {
        UIManager.register(comp)
        return comp
    }

    fun register(comp: UIComponent, index: Int) = UIManager.register(comp, index)
    fun get(id: String) = UIManager.getOrNull(id)


    init {
        if (!isUndecorated) {
            UIManager.register(HangBar("HangBar-${UUID.randomUUID()}", Rect(0, 0, 0, 0), this))
        }
    }
}

class UIManager(val ndhsDisplay: NDHSDisplay) {
    val UIComponent = mutableListOf<UIComponent>()

    fun register(component: UIComponent) = register(component, 0)
    fun register(component: UIComponent, index: Int) {
        if (getOrNull(component.name) != null) throw Exception("The Component ID ${component.name} is already taken")

        //before子コンポーネント登録
        component.before()?.forEach {
            register(it, index)
        }

        UIComponent.add(component)
        ndhsDisplay.jFrame.scene().layer(index).add(component)
        component.onAdded(ndhsDisplay)

        // after子コンポーネント登録
        component.after()?.forEach {
            register(it, index)
        }
    }

    fun getOrNull(id: String) = UIComponent.filter { it.name == id }.getOrNull(0)

    fun remove(id: String, index: Int) {
        val d = getOrNull(id)
        if (d != null) remove(d, index)
    }

    fun remove(id: String) {
        val d = getOrNull(id)
        if (d != null) remove(d)
    }

    fun remove(component: UIComponent) {
        UIComponent.remove(component)
        for (layer in ndhsDisplay.jFrame.scene().layers) {
            layer.drawables.remove(component)
        }
    }

    fun remove(component: UIComponent, index: Int) {
        UIComponent.remove(component)
        ndhsDisplay.jFrame.scene().layer(index).drawables.remove(component)
    }
}

private fun Scene.layer(index: Int): Layer {
    if (index < 0) throw IndexOutOfBoundsException("Index $index is less than 0")
    return if (layers.getOrNull(index) == null) {
        val l = Layer()
        layers.add(index, l)
        l
    } else {
        layers[index]
    }
}
