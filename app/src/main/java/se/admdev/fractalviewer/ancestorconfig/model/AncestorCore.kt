package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import se.admdev.fractalviewer.canvas.model.Cell
import se.admdev.fractalviewer.canvas.model.Coord

@Parcelize
class AncestorCore(val configNodes: List<ConfigNode>) : Parcelable {

    var function: ((Coord, List<Cell>) -> Int)? = null

    init {
        function = configNodes.last().compile(configNodes)
    }

    fun calculateValue(currentCell: Coord, ancestors: List<Cell>): Int {
//        return ancestors.fold(0) { acc, cell -> (acc + cell.value) % 2 }
        return function?.invoke(currentCell, ancestors) ?: 0
    }

    //TODO only supports square ancestor areas atm // TODO remove this calculation and specify it explicitly
    val width: Int = (configNodes.firstOrNull() as? OperationConfigNode)?.gridSize ?: 0
    val height: Int = width
    val midX = width / 2
}
