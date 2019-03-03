package se.admdev.fractalviewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.model.ConfigTile

class ConfigurationAncestorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var configTile: ConfigTile? = null

    fun bind(tile: ConfigTile, size: Int, containerSize: Float) {
        configTile = tile
        itemView.setBackgroundColor(if (tile.x % 2 == 0 && tile.y % 2 == 0 || tile.x % 2 == 1 && tile.y % 2 == 1) 0xFFABCDEF.toInt() else 0xFFFEDCBA.toInt())

        if (tile.selected) {
            itemView.setBackgroundColor(0xF7B)
        }
        itemView.layoutParams.height = (containerSize / size).toInt()
        itemView.layoutParams.width = (containerSize / size).toInt()
    }

    companion object {

        private const val LAYOUT = R.layout.ancestor_configuration_tile

        @JvmStatic
        fun create(
            parent: ViewGroup,
            listener: ConfigurationAdapter.AncestorGridClickListener?
        ): ConfigurationAncestorViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(LAYOUT, parent, false)
            val holder = ConfigurationAncestorViewHolder(view)
            view.setOnClickListener {
                holder.configTile?.selected = !(holder.configTile?.selected ?: true)
                listener?.onTileClicked(holder.adapterPosition)
            }
            return holder
        }
    }
}