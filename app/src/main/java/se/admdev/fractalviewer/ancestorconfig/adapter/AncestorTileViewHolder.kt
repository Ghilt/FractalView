package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.AncestorTile

class AncestorTileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var configTile: AncestorTile? = null

    fun bind(tile: AncestorTile, size: Int, containerSize: Float) {
        configTile = tile

        if (tile.selected) {
            itemView.isSelected = true
        }
        itemView.layoutParams.height = (containerSize / size).toInt()
        itemView.layoutParams.width = (containerSize / size).toInt()
    }

    companion object {

        private const val LAYOUT = R.layout.ancestor_configuration_tile

        @JvmStatic
        fun create(
            parent: ViewGroup,
            listener: AncestorTileAdapter.AncestorGridClickListener?,
            clickable: Boolean
        ): AncestorTileViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(LAYOUT, parent, false)
            return AncestorTileViewHolder(view).apply {
                if (clickable) {
                    itemView.setOnClickListener {
                        configTile?.selected = !(configTile?.selected ?: true)
                        listener?.onTileClicked(adapterPosition)
                    }
                }
                setIsRecyclable(false) // Fixes animation glitch, and all items are on screen at all times anyway
            }
        }
    }
}