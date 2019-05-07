package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_saved_ancestor_core.view.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorCoreViewHolder.AncestorCoreAction.*
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.canvas.FractalThumbnailView

class AncestorCoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val name: TextView = itemView.name
    private val thumbnail: FractalThumbnailView = itemView.fractalThumbnail
    private val edit: ImageButton = itemView.edit_button
    private val show: ImageButton = itemView.show_button
    private val delete: ImageButton = itemView.delete_button

    private var core: AncestorCoreListItem? = null

    fun bind(item: AncestorCoreListItem) {
        this.core = item
        name.text = "${item.core.name}"
        thumbnail.setFractalData(item.miniatureData)
    }

    companion object {

        private const val LAYOUT = R.layout.list_item_saved_ancestor_core

        fun create(parent: ViewGroup, listener: ((AncestorCore, AncestorCoreAction) -> Unit)): AncestorCoreViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(LAYOUT, parent, false)
            return AncestorCoreViewHolder(view).apply {
                edit.setOnClickListener {
                    core?.let { listener.invoke(it.core, EDIT) }
                }
                show.setOnClickListener {
                    core?.let { listener.invoke(it.core, SHOW) }
                }
                delete.setOnClickListener {
                    core?.let { listener.invoke(it.core, DELETE) }
                }
            }
        }
    }

    enum class AncestorCoreAction {
        EDIT, SHOW, DELETE
    }
}