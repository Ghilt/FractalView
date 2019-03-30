package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.ancestorconfig.model.AncestorTile

class AncestorTileAdapter : RecyclerView.Adapter<AncestorTileViewHolder>() {

    interface AncestorGridClickListener {
        fun onTileClicked(position: AncestorTile?)
    }

    var listener: AncestorGridClickListener? = null
    var data = mutableListOf<List<AncestorTile>>()
    private val size
        get() = data.size
    var containerSize: Float = 0f

    override fun onCreateViewHolder(view: ViewGroup, type: Int) =
        AncestorTileViewHolder.create(view, listener)
    override fun getItemCount() = size * size
    override fun onBindViewHolder(view: AncestorTileViewHolder, pos: Int)
            = view.bind(getTileFromPos(pos), size, containerSize)

    fun updateGrid(items: List<List<AncestorTile>>) {

        val diffCallback = AncestorGridDiffCallback(data, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        data.clear()
        data.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun getTileFromPos(pos: Int) = data[pos.toY()][pos.toX()]
    private fun Int.toX() = this % size
    private fun Int.toY() = this / size
}