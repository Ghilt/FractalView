package se.admdev.fractalviewer.ancestorconfig.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import se.admdev.fractalviewer.canvas.model.Cell

@Parcelize
class AncestorCore(val configNodes: List<ConfigNode> ) : Parcelable {

    fun calculateValue(ancestors: List<Cell>): Int {
        return ancestors.fold(0) { acc, cell -> (acc + cell.value) % 2 }
    }

    //TODO debugg
    val width: Int = 7
    val height: Int = 7
    val midX = width / 2
}
