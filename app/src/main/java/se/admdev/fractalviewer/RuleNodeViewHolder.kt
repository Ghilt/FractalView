package se.admdev.fractalviewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_rule_node.view.*
import se.admdev.fractalviewer.model.ConfigurationNode

class RuleNodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val sumOf: TextView = itemView.node_origin
    private val grid: RecyclerView = itemView.ancestor_grid_miniature
    private var adapter: ConfigurationAdapter? = null

    fun bind(node: ConfigurationNode) {
        (grid.layoutManager as GridLayoutManager).spanCount = node.tileSnapshot.size
        adapter?.setDataSet(node.tileSnapshot)
        adapter?.notifyDataSetChanged()
    }

    companion object {

        private const val LAYOUT = R.layout.list_item_rule_node

        @JvmStatic
        fun create(parent: ViewGroup): RuleNodeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(LAYOUT, parent, false)
            return RuleNodeViewHolder(view).apply {
                adapter = ConfigurationAdapter()
                grid.layoutManager = GridLayoutManager(itemView.context, 1)
                adapter?.containerSize = itemView.resources.getDimension(R.dimen.grid_size_miniature)
                grid.adapter = adapter
            }
        }
    }
}