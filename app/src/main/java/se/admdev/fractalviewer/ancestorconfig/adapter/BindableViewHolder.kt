package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.ancestorconfig.adapter.ConfigurationListAdapter.Companion.VIEW_TYPE_CONDITION
import se.admdev.fractalviewer.ancestorconfig.adapter.ConfigurationListAdapter.Companion.VIEW_TYPE_GROUP_OPERATION
import se.admdev.fractalviewer.ancestorconfig.adapter.ConfigurationListAdapter.Companion.VIEW_TYPE_OPERATION
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode

abstract class BindableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var boundNode: ConfigNode? = null

    //Trying out this pattern to get rid of 'when' structure with view type in adapter bind
    abstract fun <T : ConfigNode> bind(node: T)

    companion object {
        fun create(parent: ViewGroup, type: Int, listener: ((ConfigNode) -> Unit)): BindableViewHolder {
            return when (type) {
                VIEW_TYPE_OPERATION -> OperationViewHolder.create(parent)
                VIEW_TYPE_GROUP_OPERATION -> GroupOperationViewHolder.create(parent)
                VIEW_TYPE_CONDITION -> ConditionViewHolder.create(parent)
                else -> object : BindableViewHolder(parent) {
                    override fun <T : ConfigNode> bind(node: T) {}
                }
            }.apply {
                this.itemView.setOnClickListener {
                    boundNode?.let { listener.invoke(it) }
                }
            }
        }
    }
}