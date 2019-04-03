package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_operation_node.view.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode
import se.admdev.fractalviewer.ancestorconfig.model.OperationConfigNode
import se.admdev.fractalviewer.getDarkTintColorStateList
import se.admdev.fractalviewer.showLabel

class OperationViewHolder(itemView: View) : BindableViewHolder(itemView) {

    private val label: TextView = itemView.label
    private val firstOperandText: TextView = itemView.first_operand_text
    private val operatorText: TextView = itemView.operator_text
    private val secondOperandText: TextView = itemView.second_operand_text
    private var adapter: AncestorTileAdapter? = null

    override fun <T : ConfigNode> bind(node: T) {
        val n = node as OperationConfigNode

        label.showLabel(n.label)
        operatorText.text = n.operator.symbol

        if (n.firstOperand.label != null) {
            firstOperandText.showLabel(n.firstOperand.label)
        } else {
            firstOperandText.text = n.firstOperand.name
            firstOperandText.backgroundTintList = getDarkTintColorStateList(itemView)
        }

        if (n.secondOperand.label != null) {
            secondOperandText.showLabel(n.secondOperand.label)
        } else {
            secondOperandText.text = n.secondOperand.name
            secondOperandText.backgroundTintList = getDarkTintColorStateList(itemView)
        }
    }

    companion object {

        private const val LAYOUT = R.layout.list_item_operation_node

        @JvmStatic
        fun create(parent: ViewGroup): BindableViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(LAYOUT, parent, false)
            return OperationViewHolder(view)
        }
    }
}