package se.admdev.fractalviewer.ancestorconfig.adapter

import androidx.recyclerview.widget.DiffUtil
import se.admdev.fractalviewer.ancestorconfig.model.AncestorTile

class AncestorGridDiffCallback(private val oldGrid: List<List<AncestorTile>>, private val newGrid: List<List<AncestorTile>>) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldGrid[oldPos.toOldX()][oldPos.toOldY()] === newGrid[newPos.toNewX()][newPos.toNewY()]
    }

    override fun getOldListSize(): Int = oldGrid.size * oldGrid.size
    override fun getNewListSize(): Int = newGrid.size * newGrid.size

    override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldGrid[oldPos.toOldX()][oldPos.toOldY()] == newGrid[newPos.toNewX()][newPos.toNewY()]
    }

    private fun Int.toOldX() = this % oldGrid.size
    private fun Int.toOldY() = this / oldGrid.size
    private fun Int.toNewX() = this % newGrid.size
    private fun Int.toNewY() = this / newGrid.size
}