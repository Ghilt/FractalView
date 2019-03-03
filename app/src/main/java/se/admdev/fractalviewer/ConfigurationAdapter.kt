package se.admdev.fractalviewer

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.model.ConfigTile

class ConfigurationAdapter : RecyclerView.Adapter<ConfigurationAncestorViewHolder>() {

    interface AncestorGridClickListener {
        fun onTileClicked(position: Int)
    }

    var listener: AncestorGridClickListener? = null
    var data = listOf<List<ConfigTile>>()
    private val size
        get() = data.size
    var containerSize: Float = 0f

    override fun onCreateViewHolder(view: ViewGroup, type: Int) = ConfigurationAncestorViewHolder.create(view, listener)
    override fun getItemCount() = size * size
    override fun onBindViewHolder(view: ConfigurationAncestorViewHolder, pos: Int) =
        view.bind(getTileFromPos(pos), size, containerSize)

    fun setDataSet(items: List<List<ConfigTile>>) {
        data = items
    }

    private fun getTileFromPos(pos: Int) = data[pos.toY()][pos.toX()]
    private fun Int.toX() = this % size
    private fun Int.toY() = this / size
}