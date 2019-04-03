package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.ancestorconfig.adapter.ConfigurationListAdapter.Companion.VIEW_TYPE_CONDITION
import se.admdev.fractalviewer.ancestorconfig.adapter.ConfigurationListAdapter.Companion.VIEW_TYPE_GROUP_OPERATION
import se.admdev.fractalviewer.ancestorconfig.adapter.ConfigurationListAdapter.Companion.VIEW_TYPE_OPERATION
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode

abstract class BindableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    //Trying out this pattern to get rid of 'when' structure with view type in adapter bind
    abstract fun <T : ConfigNode> bind(node: T)

    companion object {
        fun create(itemView: ViewGroup, type: Int): BindableViewHolder {
            return when (type) {
                VIEW_TYPE_OPERATION -> OperationViewHolder.create(itemView)
                VIEW_TYPE_GROUP_OPERATION -> GroupOperationViewHolder.create(itemView)
                VIEW_TYPE_CONDITION -> ConditionViewHolder.create(itemView)
                else -> object : BindableViewHolder(itemView) {
                    override fun <T : ConfigNode> bind(node: T) {}
                }
            }
        }
    }
}