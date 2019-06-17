package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_glossary_entry.view.*
import kotlinx.android.synthetic.main.list_item_saved_ancestor_core.view.name
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.OperatorData

class GlossaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val name: TextView = itemView.name
    private val description: TextView = itemView.description

    fun bind(item: OperatorData) {
        name.text = item.symbol
        description.text = itemView.resources.getString(item.description)
    }

    companion object {

        private const val LAYOUT = R.layout.list_item_glossary_entry

        fun create(parent: ViewGroup): GlossaryViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(LAYOUT, parent, false)
            return GlossaryViewHolder(view)
        }
    }
}