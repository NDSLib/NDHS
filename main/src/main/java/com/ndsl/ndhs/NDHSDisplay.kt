package com.ndsl.ndhs

import com.github.bun133.nngraphics.display.JFrameDisplay
import com.github.bun133.nngraphics.display.Layer
import com.github.bun133.nngraphics.display.Rect
import com.github.bun133.nngraphics.display.Scene
import com.ndsl.ndhs.ui.UIComponent
import javax.swing.JFrame

class NDHSDisplay(val jFrame: JFrameDisplay) {
    /**
     * あほ長Constructor
     */
    constructor(
        name: String = "NNGraphics",
        bound: Rect,
        closeOperation: Int = JFrame.EXIT_ON_CLOSE,
        bufferSize: Int = 3,
        isUndecorated: Boolean = false
    ) : this(JFrameDisplay(name, bound, closeOperation, bufferSize, isUndecorated))

    val UIManager = UIManager(this)
}

class UIManager(val ndhsDisplay: NDHSDisplay) {
    private val UIComponent = mutableListOf<UIComponent>()

    fun register(component: UIComponent) = register(component, 0)
    fun register(component: UIComponent, index: Int) {
        if (getOrNull(component.name) != null) throw Exception("The Component ID ${component.name} is already taken")
        UIComponent.add(component)
        ndhsDisplay.jFrame.scene().layer(index).add(component)
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
