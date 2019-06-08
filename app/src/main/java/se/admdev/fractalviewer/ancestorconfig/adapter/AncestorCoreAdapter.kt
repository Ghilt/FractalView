package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorCoreViewHolder.AncestorCoreAction
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore

class AncestorCoreAdapter(private val miniatureSize: Int, private val listener: ((Int, AncestorCore, AncestorCoreAction) -> Unit)) :
    RecyclerView.Adapter<AncestorCoreViewHolder>() {

//    init {
//        setHasStableIds(true)
//    }

    private var data = listOf<AncestorCoreListItem>()

    override fun onCreateViewHolder(view: ViewGroup, type: Int): AncestorCoreViewHolder =
        AncestorCoreViewHolder.create(view, listener)

    override fun getItemCount() = data.size
    override fun onBindViewHolder(view: AncestorCoreViewHolder, pos: Int) {
        view.bind(data[pos])
    }

//    override fun getItemId(position: Int): Long {
//        return data[position].name.toLong()
//    }

    fun setDataSet(items: List<AncestorCore>) {
        data = items.mapIndexed { i, core -> AncestorCoreListItem(miniatureSize, core) { notifyItemChanged(i) } }
    }

    fun removeItem(position: Int, core: AncestorCore) {
        data = data.filter { it.core.name != core.name }
        notifyItemRemoved(position)
    }
}