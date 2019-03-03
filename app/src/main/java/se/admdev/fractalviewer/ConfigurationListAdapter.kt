package se.admdev.fractalviewer

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.model.ConfigurationNode

class ConfigurationListAdapter : RecyclerView.Adapter<RuleNodeViewHolder>() {

    private var data = listOf<ConfigurationNode>()

    override fun onCreateViewHolder(view: ViewGroup, type: Int) = RuleNodeViewHolder.create(view)
    override fun getItemCount() = data.size
    override fun onBindViewHolder(view: RuleNodeViewHolder, pos: Int) = view.bind(data[pos])
    fun setDataSet(items: List<ConfigurationNode>) {
        data = items
    }
}