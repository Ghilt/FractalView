package se.admdev.fractalviewer.ancestorconfig

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.admdev.fractalviewer.ancestorconfig.model.AncestorTile
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode

private const val ANCESTOR_TILE_INITIAL_SIZE = 3

class ConfigViewModel : ViewModel() {

    val configNodes = MutableLiveData<MutableList<ConfigNode>>().apply { value = mutableListOf() }
    val ancestorTiles = MutableLiveData<List<List<AncestorTile>>>().apply {
        value = calculateAncestorTiles(ANCESTOR_TILE_INITIAL_SIZE)
    }

    val ancestorTileDimension: Int
        get() = ancestorTiles.value?.size ?: 0

    private fun calculateAncestorTiles(newSize: Int, oldGrid: List<List<AncestorTile>>? = null): List<List<AncestorTile>> {
        val temp = mutableListOf<List<AncestorTile>>()

        for (y in 0 until newSize) {
            val row = mutableListOf<AncestorTile>()
            for (x in 0 until newSize) {
                val tile = oldGrid
                    ?.getOrNull(y)
                    ?.getOrNull(x)
                row.add(tile ?: AncestorTile(x, y))
            }
            temp.add(row.toList())
        }

        return temp.toList()
    }

    fun increaseAncestorTiles() {
        ancestorTiles.value = calculateAncestorTiles(ancestorTileDimension + 2, ancestorTiles.value)
    }

    fun decreaseAncestorTiles() {
        ancestorTiles.value = calculateAncestorTiles(ancestorTileDimension - 2, ancestorTiles.value)
    }

    fun clearAncestorSelection() {
        // OK, lists wont be long
        ancestorTiles.value?.forEach {
            it.forEach { tile -> tile.selected = false }
        }
        ancestorTiles.triggerObserver()
    }

    fun hasSelectedTile() = ancestorTiles.value?.flatten()?.any { it.selected } ?: false
    fun getTileSnapshot() = ancestorTiles.value?.map { list -> list.map { tile -> tile.copy() } } ?: listOf()

}

fun <T> MutableLiveData<T>.triggerObserver() {
    value = value // Needed to trigger observer
}

fun <T> MutableLiveData<MutableList<T>>.addItem(item: T) {
    val updatedItems = this.value
    updatedItems?.add(item)
    triggerObserver()
}