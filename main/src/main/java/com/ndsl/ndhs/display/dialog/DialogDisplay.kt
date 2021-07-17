package com.ndsl.ndhs.display.dialog

import com.github.bun133.nngraphics.display.Pos
import com.github.bun133.nngraphics.display.Rect
import com.ndsl.ndhs.NDHS
import com.ndsl.ndhs.icon.Icon
import com.ndsl.ndhs.plugin.NDHSPlugin
import com.ndsl.ndhs.ui.Button
import com.ndsl.ndhs.ui.Label
import com.ndsl.ndhs.ui.UIPositionStyle

class DialogDisplay(val r: Rect, val icon: Icon, val str: String, val dialogChoice: DialogChoice) {
    fun show(ndhs: NDHS, plugin: NDHSPlugin?) {
        val d = ndhs.newGUIWindow(r, plugin)
        dialogChoice.getButtons(Pos(r.left_up.x,r.right_down.y - 40),100,40,10,"dialog_${plugin?.name()}")
    }

    companion object {
        //左右空白はそれぞれ10px
        const val horizontalPadding = 10

        /**
         * Dialog用のwindowサイズ計算
         *
         * 左右空白はそれぞれ10px
         * 文字列とアイコンの間にも10px
         *
         * 左側 1/3 をアイコン表示用に確保、アイコンは右側文字列に真ん中が合うように横に並べる
         *
         * 上側 2/3 をアイコンと文字列が使用
         *
         * 下側 1/3 を選択肢のボタン用に割り当て
         */
        fun getRect(string: String, iconSize: Rect) {
            // TODO デザインから逃げるな
            var width = 0
            var height = 0

            width += horizontalPadding * 3 // 左右空白はそれぞれ10px + 文字列とアイコンの間にも10px

            width += iconSize.width() //
        }
    }
}

/**
 * @param choices Pair of String(Button String) and func(when pressed)
 */
class DialogChoice(val choices: MutableList<Pair<String, () -> Unit>>) {
    fun getButtons(left_up: Pos, width: Int, height: Int, xOffset: Int, id: String) =
        choices.mapIndexed { index, pair ->
            getButton(id, pair.first, getRect(left_up, width, height, xOffset, index), pair.second)
        }

    fun getRects(left_up: Pos, width: Int, height: Int, xOffset: Int, counts: Int): MutableList<Rect> {
        val list = mutableListOf<Rect>()
        repeat(counts) {
            list.add(
                getRect(left_up, width, height, xOffset, it)
            )
        }
        return list
    }

    fun getRect(left_up: Pos, width: Int, height: Int, xOffset: Int, index: Int) = Rect(
        left_up.x + (width + xOffset) * index,
        left_up.y,
        left_up.x + (width + xOffset) * index + width,
        left_up.y + height
    )

    companion object {
        fun getButton(id: String, s: String, r: Rect, f: () -> Unit) =
            Button(
                Label("${id}_${s}_label", r, s, 12, UIPositionStyle.Center),
                "${id}_${s}_button"
            ).also { it.onClick { f() } }
    }
}