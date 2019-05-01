package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import se.admdev.fractalviewer.canvas.model.Cell
import se.admdev.fractalviewer.canvas.model.Coord

@Parcelize
class AncestorCore(@Suppress("CanBeParameter") private val configNodes: List<ConfigNode>) : Parcelable {
    //TODO I think the above warning supression is safe and that is something with Parcelize which isn't finished

    //TODO only supports square ancestor areas/single size(probably for the better) atm
    @IgnoredOnParcel
    val width: Int = (configNodes.firstOrNull() as? GroupOperationConfigNode)?.gridSize ?: 0
    @IgnoredOnParcel
    val height: Int = width
    @IgnoredOnParcel
    val midX = width / 2
    @IgnoredOnParcel
    var function: ((Coord, List<Cell>) -> Int)? = configNodes.last().compile(configNodes)

    fun calculateValue(currentCell: Coord, ancestors: List<Cell>): Int {
        return function?.invoke(currentCell, ancestors) ?: 0
    }
}
