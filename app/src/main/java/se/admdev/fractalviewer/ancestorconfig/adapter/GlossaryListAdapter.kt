package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.ancestorconfig.model.OperatorData

class GlossaryListAdapter : RecyclerView.Adapter<GlossaryViewHolder>() {

    init { setHasStableIds(true) }

    private var data = listOf<OperatorData>()

    override fun onCreateViewHolder(view: ViewGroup, type: Int): GlossaryViewHolder = GlossaryViewHolder.create(view)

    override fun getItemCount() = data.size
    override fun onBindViewHolder(view: GlossaryViewHolder, pos: Int) {
        view.bind(data[pos])
    }

    override fun getItemId(position: Int): Long {
        return data[position].symbol.hashCode().toLong()
    }

    fun setDataSet(items: List<OperatorData>) {
        data = items
    }
}