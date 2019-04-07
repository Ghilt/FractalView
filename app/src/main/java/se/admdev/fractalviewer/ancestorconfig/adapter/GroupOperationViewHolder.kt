package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_group_operation_node.view.*
import se.admdev.fractalviewer.*
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode
import se.admdev.fractalviewer.ancestorconfig.model.GroupOperationConfigNode

class GroupOperationViewHolder(itemView: View) : ConfigNodeViewHolder(itemView) {

    private val label: TextView = itemView.label
    private val groupOperatorText: TextView = itemView.group_operator_text
    private val grid: RecyclerView = itemView.ancestor_grid_miniature
    private val operatorText: TextView = itemView.operator_text
    private val operandText: TextView = itemView.operand_text
    private var adapter: AncestorTileAdapter? = null

    override fun <T : ConfigNode> bind(node: T) {
        super.bind(node)
        val n = node as GroupOperationConfigNode

        label.showLabel(n.label)

        groupOperatorText.text = itemView.context.getString(R.string.node_list_group_operator, n.groupOperator.symbol)

        grid.gridLayoutManager.spanCount = n.tileSnapshot.size
        adapter?.setDataSet(n.tileSnapshot)
        adapter?.notifyDataSetChanged()

        if (n.operator != null && n.operand != null) {
            operatorText.text = itemView.context.getString(R.string.node_list_sum_all_end_bracket, n.operator.symbol)
            operandText.setVisible()
            if (n.operand.label != null) {
                operandText.showLabel(n.operand.label)
            } else {
                operandText.text = n.operand.name
                operandText.backgroundTintList = getDarkTintColorStateList(itemView)
            }
        } else {
            operatorText.text = itemView.context.getString(R.string.node_list_sum_all_end_bracket, "")
            operandText.setGone()
        }
    }

    companion object {

        private const val LAYOUT = R.layout.list_item_group_operation_node

        @JvmStatic
        fun create(parent: ViewGroup): ConfigNodeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(LAYOUT, parent, false)
            return GroupOperationViewHolder(view).apply {
                adapter = AncestorTileAdapter()
                adapter?.containerSize = itemView.resources.getDimension(R.dimen.grid_size_miniature)
                grid.adapter = adapter
            }
        }
    }
}