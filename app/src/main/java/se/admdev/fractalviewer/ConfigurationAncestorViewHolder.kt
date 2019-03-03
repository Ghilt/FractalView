package se.admdev.fractalviewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ConfigurationAncestorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(pos: Int, size: Int, containerSize: Float) {
        itemView.setBackgroundColor(if (pos % 2 == 0) 0xFFABCDEF.toInt() else 0xFFFEDCBA.toInt())

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
            view.setOnClickListener { listener?.onTileClicked(holder.adapterPosition) }
            return holder
        }
    }
}