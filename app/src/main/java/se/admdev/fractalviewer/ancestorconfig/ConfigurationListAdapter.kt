package se.admdev.fractalviewer.ancestorconfig

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode

class ConfigurationListAdapter : RecyclerView.Adapter<ConfigNodeViewHolder>() {

    private var data = listOf<ConfigNode>()

    override fun onCreateViewHolder(view: ViewGroup, type: Int) =
        ConfigNodeViewHolder.create(view)
    override fun getItemCount() = data.size
    override fun onBindViewHolder(view: ConfigNodeViewHolder, pos: Int) = view.bind(data[pos])
    fun setDataSet(items: List<ConfigNode>) {
        data = items
    }
}