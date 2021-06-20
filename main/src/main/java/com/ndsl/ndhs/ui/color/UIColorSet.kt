package com.ndsl.ndhs.ui.color

import com.ndsl.ndhs.ui.UIComponent
import java.awt.Color

/**
 * UIComponent用のColorSet
 * どのColorを使うかはComponent次第。
 */
abstract class UIColorSet(val comp: UIComponent) {
    open var mainColor: Color = Color.BLACK
    open var accentColor: Color = Color.BLACK
    open var backGroundColor: Color = Color.BLACK
    open var actionColor: Color = Color.CYAN
}