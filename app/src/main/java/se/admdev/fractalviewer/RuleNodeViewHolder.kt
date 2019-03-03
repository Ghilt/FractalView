package se.admdev.fractalviewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RuleNodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(pos: Int) {
        itemView.setBackgroundColor(if (pos % 2 == 0) 0xFFFBCDCD.toInt() else 0xFF4EDCFA.toInt())
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