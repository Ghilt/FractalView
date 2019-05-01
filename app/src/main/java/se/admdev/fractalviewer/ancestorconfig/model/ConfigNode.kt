package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import se.admdev.fractalviewer.canvas.model.Cell
import se.admdev.fractalviewer.canvas.model.Coord

/** Deserialization currently requires one unique field to identify correct subclass
 *  Check subclass companion object for further info
 */
@Parcelize
open class ConfigNode(
    val label: Char,
    var selected: Boolean = false
) : Parcelable {

    open fun compile(nodes: List<ConfigNode>): ((Coord, List<Cell>) -> Int) = { _, _ -> 0}

    fun getNodeWithLabel(nodes: List<ConfigNode>, label: Char): ConfigNode = nodes.first { it.label == label }
}