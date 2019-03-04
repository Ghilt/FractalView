package se.admdev.fractalviewer.ancestorconfig

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.ancestorconfig.model.AncestorTile

class AncestorTileAdapter : RecyclerView.Adapter<AncestorTileViewHolder>() {

    interface AncestorGridClickListener {
        fun onTileClicked(position: Int)
    }

    var listener: AncestorGridClickListener? = null
    var data = listOf<List<AncestorTile>>()
    private val size
        get() = data.size
    var containerSize: Float = 0f

    override fun onCreateViewHolder(view: ViewGroup, type: Int) =
        AncestorTileViewHolder.create(view, listener)
    override fun getItemCount() = size * size
    override fun onBindViewHolder(view: AncestorTileViewHolder, pos: Int) =
        view.bind(getTileFromPos(pos), size, containerSize)

    fun setDataSet(items: List<List<AncestorTile>>) {
        data = items
    }

    private fun getTileFromPos(pos: Int) = data[pos.toY()][pos.toX()]
    private fun Int.toX() = this % size
    private fun Int.toY() = this / size
}