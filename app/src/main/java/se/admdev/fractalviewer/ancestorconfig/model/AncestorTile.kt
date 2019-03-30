package se.admdev.fractalviewer.ancestorconfig.model

data class AncestorTile(val x: Int, val y: Int, val selected: Boolean = false) {
    fun samePos(target: AncestorTile): Boolean = x == target.x && y == target.y
}