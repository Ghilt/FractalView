package se.admdev.fractalviewer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.admdev.fractalviewer.model.ConfigTile
import se.admdev.fractalviewer.model.ConfigurationNode

private const val ANCESTOR_TILE_INITIAL_SIZE = 3

class ConfigurationViewModel : ViewModel() {

    val configNodes = MutableLiveData<MutableList<ConfigurationNode>>().apply { value = mutableListOf() }
    val ancestorTiles = MutableLiveData<List<List<ConfigTile>>>().apply {
        value = calculateAncestorTiles(ANCESTOR_TILE_INITIAL_SIZE)
    }

    val ancestorTileDimension: Int
        get() = ancestorTiles.value?.size ?: 0

    private fun calculateAncestorTiles(newSize: Int, oldGrid: List<List<ConfigTile>>? = null): List<List<ConfigTile>> {
        val temp = mutableListOf<List<ConfigTile>>()

        for (y in 0 until newSize) {
            val row = mutableListOf<ConfigTile>()
            for (x in 0 until newSize) {
                val tile = oldGrid
                    ?.getOrNull(y)
                    ?.getOrNull(x)
                row.add(tile ?: ConfigTile(x, y))
            }
            temp.add(row.toList())
        }

        return temp.toList()
    }

    fun increaseAncestorTiles() {
        ancestorTiles.value = calculateAncestorTiles(ancestorTileDimension + 2)
    }

    fun decreaseAncestorTiles() {
        ancestorTiles.value = calculateAncestorTiles(ancestorTileDimension - 2)
    }

    fun clearAncestorSelection() {
        // OK, lists wont be long
        ancestorTiles.value?.forEach {
            it.forEach { tile -> tile.selected = false }
        }
        ancestorTiles.triggerObserver()
    }

    fun hasSelectedTile() = ancestorTiles.value?.flatten()?.any { it.selected } ?: false
    fun getSelectedTiles() = ancestorTiles.value?.flatten()?.filter { it.selected } ?: listOf()

}

fun <T> MutableLiveData<T>.triggerObserver() {
    value = value // Needed to trigger observer
}

fun <T> MutableLiveData<MutableList<T>>.addItem(item: T) {
    val updatedItems = this.value
    updatedItems?.add(item)
    triggerObserver()
}