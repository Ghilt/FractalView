package se.admdev.fractalviewer.ancestorconfig

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.admdev.fractalviewer.ancestorconfig.model.*
import se.admdev.fractalviewer.getLabelColor

private const val ANCESTOR_TILE_INITIAL_SIZE = 3
private const val ANCESTOR_TILE_MIN_SIZE = 3
private const val ANCESTOR_TILE_MAX_SIZE = 7

class ConfigViewModel : ViewModel() {

    val configNodes = MutableLiveData<MutableList<ConfigNode>>().apply { value = mutableListOf() }
    val ancestorTiles = MutableLiveData<List<List<AncestorTile>>>().apply {
        value = calculateAncestorTiles(ANCESTOR_TILE_INITIAL_SIZE)
    }
    val ancestorTileDimension: Int
        get() = ancestorTiles.value?.size ?: 0

    init {

        //TODO TEMP DEBUG data initiation
        repeat(7) {
            configNodes.addItem(
                GroupOperationConfigNode(
                    getNextNodeLabel(),
                    GroupOperator.SUM,
                    getTileSnapshot(),
                    null,
                    null
                )
            )
        }
    }

    private fun calculateAncestorTiles(
        newSize: Int,
        oldGrid: List<List<AncestorTile>>? = null
    ): List<List<AncestorTile>> {
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
        if (!isAncestorGridMinSize()) {
            ancestorTiles.value = calculateAncestorTiles(ancestorTileDimension - 2, ancestorTiles.value)
        }
    }

    fun isAncestorGridMinSize() = ancestorTileDimension == ANCESTOR_TILE_MIN_SIZE
    fun isAncestorGridMaxSize() = ancestorTileDimension == ANCESTOR_TILE_MAX_SIZE

    fun clearNodeCreationData() {
        // OK, lists wont be long
        ancestorTiles.value?.forEach {
            it.forEach { tile -> tile.selected = false }
        }
        ancestorTiles.triggerObserver()
    }

    fun selectAll() {
        ancestorTiles.value?.forEach {
            it.forEach { tile -> tile.selected = true }
        }
        ancestorTiles.triggerObserver()
    }

    fun hasSelectedTile() = ancestorTiles.value?.flatten()?.any { it.selected } ?: false
    fun getTileSnapshot() = ancestorTiles.value?.map { list -> list.map { tile -> tile.copy() } } ?: listOf()
    private fun getNextNodeLabel(): Char = 'A' + (configNodes.value?.size ?: 0)

    fun getAvailableOperandsArrayList() = ArrayList(getAvailableOperands() ?: listOf())
    private fun getAvailableOperands() = configNodes.value?.map {
        val name = it.label.toString()
        CompactPickerItem(Operand(it), name) { this.setBackgroundColor(it.label.getLabelColor()) }
    }

    fun hasSelectedConfigNode() = configNodes.value?.any { it.selected } ?: false

    fun saveNewOperationNode(firstOperand: Operand?, operator: Operator?, secondOperand: Operand?): Boolean {
        return if (firstOperand != null && operator != null && secondOperand != null) {
            configNodes.addItem(
                OperationConfigNode(
                    getNextNodeLabel(),
                    firstOperand,
                    operator,
                    secondOperand
                )
            )
            true
        } else {
            false
        }
    }

    fun saveNewGroupOperationNode(groupOperator: GroupOperator?, operator: Operator?, operand: Operand?): Boolean {
        return if (operator != null && operand == null) {
            false
        } else {
            configNodes.addItem(
                GroupOperationConfigNode(
                    getNextNodeLabel(),
                    groupOperator ?: GroupOperator.SUM,
                    getTileSnapshot(),
                    operator,
                    operand
                )
            )
            true
        }
    }

    fun saveNewConditionNode(
        condition: Operand?,
        truePath: Operand?,
        falsePath: Operand?
    ): Boolean {
        return if (condition != null && truePath != null && falsePath != null) {
            configNodes.addItem(ConditionalConfigNode(getNextNodeLabel(), condition, truePath, falsePath))
            true
        } else {
            false
        }
    }

    fun isChangeGridSizeEnabled(): Boolean = configNodes.value.isNullOrEmpty()

    fun clearConfigNodeSelection() {
        configNodes.value?.forEach { it.selected = false }
        configNodes.triggerObserver()
    }

    fun changeConfigNodeSelection(deselectFirstAndSelectSecond: Pair<Operand?, Operand?>) {
        configNodes.value?.firstOrNull { it.label == deselectFirstAndSelectSecond.first?.label }?.selected = false
        configNodes.value?.firstOrNull { it.label == deselectFirstAndSelectSecond.second?.label }?.selected = true
        configNodes.triggerObserver()
    }
}

fun <T> MutableLiveData<T>.triggerObserver() {
    value = value // Needed to trigger observer
}

fun <T> MutableLiveData<MutableList<T>>.addItem(item: T) {
    val updatedItems = this.value
    updatedItems?.add(item)
    triggerObserver()
}