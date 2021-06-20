package com.ndsl.ndhs.font

import java.awt.Font

const val defaultFontName = Font.SANS_SERIF

class FontBuilder() {
    var fontName = defaultFontName
    var fontSize = 12
    var fontStyle = 0
    fun build() = Font(fontName, fontStyle, fontSize)

    fun addStyle(style:Int){

    }
}