package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode
import se.admdev.fractalviewer.ancestorconfig.model.GroupOperationConfigNode
import se.admdev.fractalviewer.ancestorconfig.model.OperationConfigNode

class ConfigurationListAdapter(private val listener: ((ConfigNode, Boolean, Boolean, Boolean) -> Unit)) :
    RecyclerView.Adapter<ConfigNodeViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private var data = listOf<ConfigNode>()

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is OperationConfigNode -> VIEW_TYPE_OPERATION
            is GroupOperationConfigNode -> VIEW_TYPE_GROUP_OPERATION
            else -> VIEW_TYPE_CONDITION
        }
    }

    override fun onCreateViewHolder(view: ViewGroup, type: Int): ConfigNodeViewHolder =
        ConfigNodeViewHolder.create(view, type, listener)

    override fun getItemCount() = data.size
    override fun onBindViewHolder(view: ConfigNodeViewHolder, pos: Int) {
        view.bind(data[pos])
    }

    override fun getItemId(position: Int): Long {
        return data[position].label.toLong()
    }

    fun setDataSet(items: List<ConfigNode>) {
        data = items
    }

    companion object {
        const val VIEW_TYPE_OPERATION = 0
        const val VIEW_TYPE_CONDITION = 1
        const val VIEW_TYPE_GROUP_OPERATION = 2
    }
}