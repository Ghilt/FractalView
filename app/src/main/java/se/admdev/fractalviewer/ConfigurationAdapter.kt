package se.admdev.fractalviewer

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ConfigurationAdapter : RecyclerView.Adapter<ConfigurationAncestorViewHolder>() {

    interface AncestorGridClickListener{
        fun onTileClicked(position: Int)
    }

    var listener: AncestorGridClickListener? = null
    var size = 3
    var containerSize: Float = 0f

    override fun onCreateViewHolder(view: ViewGroup, type: Int) = ConfigurationAncestorViewHolder.create(view, listener)
    override fun getItemCount() = size * size
    override fun onBindViewHolder(view: ConfigurationAncestorViewHolder, pos: Int) = view.bind(pos, size, containerSize)
}