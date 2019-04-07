package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_condition_node.view.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.ConditionalConfigNode
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode
import se.admdev.fractalviewer.getDarkTintColorStateList
import se.admdev.fractalviewer.showLabel

class ConditionViewHolder(itemView: View) : ConfigNodeViewHolder(itemView) {

    private val label: TextView = itemView.label
    private val operandCondition: TextView = itemView.operand_condition_text
    private val operandTrue: TextView = itemView.operand_true_text
    private val operandFalse: TextView = itemView.operand_false_text

    override fun <T : ConfigNode> bind(node: T) {
        val n = node as ConditionalConfigNode
        boundNode = n

        label.showLabel(n.label)
        operandCondition.showLabel(n.operandCondition.label)

        if (n.operandTrue.label != null) {
            operandTrue.showLabel(n.operandTrue.label)
        } else {
            operandTrue.text = n.operandTrue.name
            operandTrue.backgroundTintList = getDarkTintColorStateList(itemView)
        }

        if (n.operandFalse.label != null) {
            operandFalse.showLabel(n.operandFalse.label)
        } else {
            operandFalse.text = n.operandFalse.name
            operandFalse.backgroundTintList = getDarkTintColorStateList(itemView)
        }
    }

    companion object {

        private const val LAYOUT = R.layout.list_item_condition_node

        @JvmStatic
        fun create(parent: ViewGroup): ConfigNodeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(LAYOUT, parent, false)
            return ConditionViewHolder(view)
        }
    }
}