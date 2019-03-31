package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import se.admdev.fractalviewer.canvas.model.Cell
import se.admdev.fractalviewer.canvas.model.Coord

@Parcelize
open class ConfigNode(
    val label: Char
) : Parcelable {

    open fun gridSize() = 0
    open fun compile(nodes: List<ConfigNode>): ((Coord, List<Cell>) -> Int) = { _, _ -> 0}

    fun getNodeWithLabel(nodes: List<ConfigNode>, label: Char): ConfigNode = nodes.first { it.label == label }
}