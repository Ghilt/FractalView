package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import se.admdev.fractalviewer.canvas.model.Cell
import se.admdev.fractalviewer.canvas.model.Coord

@Parcelize
class AncestorCore(@Suppress("CanBeParameter") val configNodes: List<ConfigNode>, val name: String? = null) : Parcelable {
    //TODO I think the above warning supression is safe and that is something with Parcelize which isn't finished

    @IgnoredOnParcel
    var userDivideByZeroError: String? = null

    //TODO only supports square ancestor areas/single size(probably for the better) atm
    @IgnoredOnParcel
    val width: Int = (configNodes.firstOrNull() as? GroupOperationConfigNode)?.gridSize ?: 0
    @IgnoredOnParcel
    val height: Int = width
    @IgnoredOnParcel
    val midX = width / 2
    @IgnoredOnParcel
    @Transient // To exclude it from gson serialization
    var function: ((Coord, List<Cell>) -> Int)? = configNodes.last().compile(configNodes)

    fun calculateValue(currentCell: Coord, ancestors: List<Cell>): Int {
        return try {
            function?.invoke(currentCell, ancestors) ?: 0
        } catch (e: Exception) {
            userDivideByZeroError = e.message
            1
        }
    }

    fun recompileOnDeserialization(){
        function = configNodes.last().compile(configNodes)
    }
}
