package se.admdev.fractalviewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_rule_node.view.*
import se.admdev.fractalviewer.model.ConfigurationNode

class RuleNodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val sumOf: TextView = itemView.node_origin

        fun bind(node: ConfigurationNode) {
                itemView.setBackgroundColor(if (node.selectedTiles.size % 2 == 0) 0xFFFBCDCD.toInt() else 0xFF4EDCFA.toInt())
                sumOf.text = node.selectedTiles.joinToString { tile -> "(${tile.x}, ${tile.y})" }
    }

    companion object {

        private const val LAYOUT = R.layout.list_item_rule_node

        @JvmStatic
        fun create(parent: ViewGroup): RuleNodeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(LAYOUT, parent, false)
            return RuleNodeViewHolder(view)
        }
    }
}