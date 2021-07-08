package com.ndsl.ndhs.icon

import com.github.bun133.nngraphics.display.Rect
import com.ndsl.ndhs.NDHS
import com.ndsl.ndhs.manager.Registry
import com.ndsl.ndhs.util.StaticNamed
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO


abstract class Icon : StaticNamed {
    // Rect内いっぱいに描画
    abstract fun draw(r: Rect, g: Graphics)
}

class BufferedImageIcon(val image: BufferedImage, n: String) : Icon() {
    override fun draw(r: Rect, g: Graphics) {
        g.drawImage(image, r.left_up.x, r.left_up.y, r.width(), r.height(), null)
    }

    override val name = n
}

class PathIcon(val path: File, n: String) : Icon() {
    var img: BufferedImage? = null
    override fun draw(r: Rect, g: Graphics) {
        if (img == null) {
            try {
                img = ImageIO.read(path)
            } catch (e: IOException) {
                println("In PathIcon,IOException Occurred,IconName:${name}")
                throw e
            }
        }
        g.drawImage(img, r.left_up.x, r.left_up.y, r.width(), r.height(), null)
    }

    override val name = n
}

class IconManager(val ndhs: NDHS) {
    val icons = Registry<Icon>()

    init {
        DefaultIcons.values().forEach {
            icons.add(it.icon)
        }
    }
}

enum class DefaultIcons(val icon: PathIcon) {
    Notice(load("notice"));
}

const val IconFolder = "main\\src\\main\\resources\\icons"
fun load(s: String) = load(File("$IconFolder\\$s.png"))
fun load(file: File): PathIcon {
    if (file.isFile && file.exists()) {
        return load(file, file.nameWithoutExtension)
    } else {
        throw Exception("In DefaultIcons#load,File${file} is not a file/exists")
    }
}

fun load(file: File, name: String) = PathIcon(file, name)
