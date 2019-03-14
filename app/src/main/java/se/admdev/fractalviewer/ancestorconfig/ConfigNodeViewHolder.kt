package se.admdev.fractalviewer.ancestorconfig

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_rule_node.view.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode
import se.admdev.fractalviewer.getLabelColor
import se.admdev.fractalviewer.gridLayoutManager

class ConfigNodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val label: TextView = itemView.label
    private val operatorText: TextView = itemView.operator_text
    private val grid: RecyclerView = itemView.ancestor_grid_miniature
    private var adapter: AncestorTileAdapter? = null

    fun bind(node: ConfigNode) {
        grid.gridLayoutManager.spanCount = node.tileSnapshot.size
        adapter?.setDataSet(node.tileSnapshot)
        adapter?.notifyDataSetChanged()

        label.setBackgroundColor(node.label.getLabelColor())
        label.text = node.label.toString()

        operatorText.text =
            itemView.context.getString(R.string.node_list_operator, node.operator?.symbol, node.operand?.name)
    }

    companion object {

        private const val LAYOUT = R.layout.list_item_rule_node

        @JvmStatic
        fun create(parent: ViewGroup): ConfigNodeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(LAYOUT, parent, false)
            return ConfigNodeViewHolder(view).apply {
                adapter = AncestorTileAdapter()
                adapter?.containerSize = itemView.resources.getDimension(R.dimen.grid_size_miniature)
                grid.adapter = adapter
            }
        }
    }
}