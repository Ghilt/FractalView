package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.ancestorconfig.adapter.ConfigurationListAdapter.Companion.VIEW_TYPE_CONDITION
import se.admdev.fractalviewer.ancestorconfig.adapter.ConfigurationListAdapter.Companion.VIEW_TYPE_GROUP_OPERATION
import se.admdev.fractalviewer.ancestorconfig.adapter.ConfigurationListAdapter.Companion.VIEW_TYPE_OPERATION
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode

abstract class ConfigNodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var boundNode: ConfigNode? = null

    //Trying out this pattern to get rid of 'when' structure with view type in adapter bind
    open fun <T : ConfigNode> bind(node: T) {
        boundNode = node
        itemView.isSelected = node.selected
    }

    companion object {
        fun create(parent: ViewGroup, type: Int, listener: ((ConfigNode, Boolean) -> Unit)): ConfigNodeViewHolder {
            return when (type) {
                VIEW_TYPE_OPERATION -> OperationViewHolder.create(parent)
                VIEW_TYPE_GROUP_OPERATION -> GroupOperationViewHolder.create(parent)
                VIEW_TYPE_CONDITION -> ConditionViewHolder.create(parent)
                else -> object : ConfigNodeViewHolder(parent) {
                    override fun <T : ConfigNode> bind(node: T) {}
                }
            }.apply {
                itemView.setOnClickListener {
                    boundNode?.let { listener.invoke(it, false) }
                }
                itemView.setOnLongClickListener {
                    boundNode?.let {
                        it.selected = !it.selected
                        listener.invoke(it, true)
                    }
                    itemView.isSelected = !itemView.isSelected
                    true
                }
            }
        }
    }
}