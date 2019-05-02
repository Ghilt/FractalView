package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_saved_ancestor_core.view.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorCoreViewHolder.AncestorCoreAction.*
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore

class AncestorCoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val name: TextView = itemView.name
    private val edit: Button = itemView.edit_button
    private val show: Button = itemView.show_button
    private val delete: Button = itemView.delete_button

    private var core: AncestorCore? = null

    fun bind(core: AncestorCore) {
        this.core = core
        name.text = "$core"
    }

    companion object {

        private const val LAYOUT = R.layout.list_item_saved_ancestor_core

        fun create(parent: ViewGroup, listener: ((AncestorCore, AncestorCoreAction) -> Unit)): AncestorCoreViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(LAYOUT, parent, false)
            return AncestorCoreViewHolder(view).apply {
                edit.setOnClickListener {
                    core?.let { listener.invoke(it, EDIT) }
                }
                show.setOnClickListener {
                    core?.let { listener.invoke(it, SHOW) }
                }
                delete.setOnClickListener {
                    core?.let { listener.invoke(it, DELETE) }
                }
            }
        }

    }

    enum class AncestorCoreAction {
        EDIT, SHOW, DELETE
    }
}